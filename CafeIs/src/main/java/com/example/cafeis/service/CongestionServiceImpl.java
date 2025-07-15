package com.example.cafeis.service;


import com.example.cafeis.DTO.CongestionStatsDTO;
import com.example.cafeis.Domain.Branch;
import com.example.cafeis.Enum.OrderStatus;
import com.example.cafeis.formatter.LocalDateFormatter;
import com.example.cafeis.repository.BranchRepository;
import com.example.cafeis.repository.OrderRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class CongestionServiceImpl implements CongestionService {

    private final OrderRepository orderRepository;
    private final BranchRepository branchRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();


    private static final int[] DEFAULT_HOURS = {7, 22};
    private static final int TIME_WINDOW_MINUTES = 15;
    private static final double ORDERS_PER_SEAT = 1.0;
    private static final double TAKEOUT_BASE_CAPACITY = 7.5;
    private static final double LOW_CONGESTION_THRESHOLD = 0.3;
    private static final double HIGH_CONGESTION_THRESHOLD = 0.6;
    private static final List<OrderStatus> ACTIVE_ORDER_STATUSES = List.of(
            OrderStatus.LOCKEDPHASE,
            OrderStatus.PREPARING

    );

    private int[] parseOperatingHours(String openScheduleJson) {
        return Optional.ofNullable(openScheduleJson)
                .filter(json -> !json.trim().isEmpty())
                .flatMap(this::extractHoursFromJson)
                .orElseGet(() -> {
                    return DEFAULT_HOURS;
                });
    }

    private Optional<int[]> extractHoursFromJson(String json) {
        return Optional.ofNullable(json)
                .map(j -> {
                    try {
                        JsonNode jsonNode = objectMapper.readTree(j);
                        int openHour = LocalTime.parse(jsonNode.get("open").asText()).getHour();
                        int closeHour = LocalTime.parse(jsonNode.get("close").asText()).getHour();

                        return new int[]{openHour, closeHour};
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull);
    }

    @Override
    public List<CongestionStatsDTO> getCongestionStats(Long branchNo) {
        List<CongestionStatsDTO> allOrders = orderRepository.getAllOrdersForDebug(branchNo);

        List<CongestionStatsDTO> rawStats = orderRepository.getCongestionStatsByBranch(branchNo, ACTIVE_ORDER_STATUSES);

        if (rawStats.isEmpty()) {
            Long todayTotal = orderRepository.getTodayOrderCountByBranch(branchNo, ACTIVE_ORDER_STATUSES);
        }

        Map<LocalDateTime, Long> groupedStats = rawStats.stream()
                .collect(Collectors.groupingBy(
                        stat -> normalizeToTimeWindow(stat.getTimeWindow()),
                        Collectors.summingLong(CongestionStatsDTO::getRequestTally)
                ));

        List<CongestionStatsDTO> result = groupedStats.entrySet().stream()
                .map(entry -> {
                    CongestionStatsDTO stat = new CongestionStatsDTO();
                    stat.setTimeWindow(entry.getKey());
                    stat.setRequestTally(entry.getValue());
                    stat.setCongestionStatus(calculateCongestionStatus(entry.getValue().intValue(), branchNo));

                    return stat;
                })
                .sorted(Comparator.comparing(CongestionStatsDTO::getTimeWindow))
                .collect(Collectors.toList());

        return result;
    }

    @Override
    public String calculateCongestionStatus(int requestTally, Long branchNo) {
        return Optional.ofNullable(branchRepository.findByBranchNo(branchNo))
                .map(branch -> {
                    return determineStatusBySeatCount(requestTally, branch.getSeatCount());
                })
                .orElseGet(() -> {
                    return "NORMAL";
                });
    }

    private String determineStatusBySeatCount(int requestTally, int seatCount) {
        double utilizationRate = calculateUtilizationRate(requestTally, seatCount);

        return Stream.of(
                        Map.entry(LOW_CONGESTION_THRESHOLD, "NORMAL"),
                        Map.entry(HIGH_CONGESTION_THRESHOLD, "BUSY"),
                        Map.entry(Double.MAX_VALUE, "VERY_BUSY")
                )
                .filter(entry -> utilizationRate <= entry.getKey())
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse("VERY_BUSY");
    }

    private double calculateUtilizationRate(int requestTally, int seatCount) {
        return seatCount == 0
                ? requestTally / TAKEOUT_BASE_CAPACITY
                : requestTally / (seatCount * ORDERS_PER_SEAT);
    }

    @Override
    public String getCongestionByTime(Long branchNo, LocalDateTime targetTime) {
        LocalDateTime normalizedTime = normalizeToTimeWindow(targetTime);
        LocalDateTime endTime = normalizedTime.plusMinutes(TIME_WINDOW_MINUTES);

        Optional.ofNullable(branchRepository.findByBranchNo(branchNo))
                .ifPresent(branch -> logBranchInfo(branch, targetTime));

        Long orderCount = orderRepository.getOrderCountByTimeRange(
                branchNo, normalizedTime, endTime, ACTIVE_ORDER_STATUSES);

        Long totalQuantity = orderRepository.getTotalItemQuantityByTimeRange(
                branchNo, normalizedTime, endTime, ACTIVE_ORDER_STATUSES);

        Long todayTotal = orderRepository.getTodayOrderCountByBranch(branchNo, ACTIVE_ORDER_STATUSES);

        logCongestionQueryResult(branchNo, targetTime, normalizedTime, endTime,
                orderCount, totalQuantity, todayTotal);

        return calculateCongestionStatus(totalQuantity.intValue(), branchNo);
    }

    private void logBranchInfo(Branch branch, LocalDateTime targetTime) {
        Optional.ofNullable(branch.getOpenScheduleJson())
                .filter(json -> !json.trim().isEmpty())
                .ifPresent(json -> logOperatingHours(json, targetTime));
    }

    private void logOperatingHours(String openScheduleJson, LocalDateTime targetTime) {
        try {
            JsonNode scheduleNode = objectMapper.readTree(openScheduleJson);
            String weekdayHours = Optional.ofNullable(scheduleNode.get("weekday"))
                    .map(JsonNode::asText)
                    .orElse("정보없음");
            String weekendHours = Optional.ofNullable(scheduleNode.get("weekend"))
                    .map(JsonNode::asText)
                    .orElse("정보없음");

            boolean isWeekend = targetTime.getDayOfWeek().getValue() >= 6;
            String todaySchedule = isWeekend ? weekendHours : weekdayHours;

            Optional.of(todaySchedule)
                    .filter(schedule -> !schedule.equals("정보없음") && schedule.contains("-"))
                    .map(schedule -> schedule.split("-"))
                    .ifPresent(times -> {});

        } catch (Exception e) {
        }
    }

    private void logCongestionQueryResult(Long branchNo, LocalDateTime targetTime,
                                          LocalDateTime normalizedTime, LocalDateTime endTime,
                                          Long orderCount, Long totalQuantity, Long todayTotal) {

        Optional.of(totalQuantity)
                .filter(quantity -> quantity == 0)
                .ifPresent(quantity -> logZeroQuantityWarning(branchNo, normalizedTime, endTime));
    }

    private void logZeroQuantityWarning(Long branchNo, LocalDateTime normalizedTime, LocalDateTime endTime) {
    }

    private LocalDateTime normalizeToTimeWindow(LocalDateTime time) {
        return time.withMinute((time.getMinute() / TIME_WINDOW_MINUTES) * TIME_WINDOW_MINUTES)
                .withSecond(0)
                .withNano(0);
    }

    private Map<String, Integer> buildOrderMap(List<CongestionStatsDTO> congestionStats) {
        return congestionStats.stream()
                .filter(stat -> stat.getTimeInterval() != null)
                .collect(Collectors.toMap(
                        stat -> convertToTimeString(stat.getTimeInterval()),
                        stat -> stat.getRequestTally().intValue(),
                        Integer::sum
                ));
    }

    private String convertToTimeString(LocalDateTime time) {
        int normalizedMinute = (time.getMinute() / TIME_WINDOW_MINUTES) * TIME_WINDOW_MINUTES;
        return String.format("%02d%02d", time.getHour(), normalizedMinute);
    }

    private String[] generateTimeWindows(int hour) {
        return IntStream.of(0, 15, 30, 45)
                .mapToObj(minute -> String.format("%02d%02d", hour, minute))
                .toArray(String[]::new);
    }

    private String formatDisplayTime(String timeInterval) {
        return Optional.ofNullable(timeInterval)
                .filter(time -> time.length() == 4)
                .flatMap(this::parseAndFormatTime)
                .orElse("");
    }

    private Optional<String> parseAndFormatTime(String timeStr) {
        return Optional.of(timeStr)
                .map(time -> {
                    try {
                        LocalTime localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HHmm"));
                        return LocalDateFormatter.formatTimeOnly(localTime.atDate(LocalDate.now()));
                    } catch (DateTimeParseException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull);
    }

    private Map<String, Object[]> generateTimeWindowPredictions(int openHour, int closeHour,
                                                                String currentTimeStr, Map<String, Integer> orderMap, Long branchNo) {

        return IntStream.range(openHour, closeHour)
                .boxed()
                .flatMap(hour -> Arrays.stream(generateTimeWindows(hour)))
                .filter(timeInterval -> Integer.parseInt(timeInterval) >= Integer.parseInt(currentTimeStr))
                .collect(Collectors.toMap(
                        this::formatDisplayTime,
                        timeInterval -> createPredictionData(timeInterval, orderMap, branchNo),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }

    private Object[] createPredictionData(String timeInterval, Map<String, Integer> orderMap, Long branchNo) {
        int requestTally = orderMap.getOrDefault(timeInterval, 0);
        String congestion = calculateCongestionStatus(requestTally, branchNo);
        return new Object[]{requestTally, congestion};
    }

    @Override
    public Map<String, Object[]> predictCongestionByTimeWindows(Long branchNo) {
        String currentTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmm"));

        Branch branch = Optional.ofNullable(branchRepository.findByBranchNo(branchNo))
                .orElseThrow(() -> new RuntimeException("지점을 검색할 수 없습니다: " + branchNo));

        Map<String, Integer> orderMap = buildOrderMap(
                orderRepository.getCongestionStatsByBranch(branchNo, ACTIVE_ORDER_STATUSES)
        );

        int[] operatingTime = parseOperatingHours(branch.getOpenScheduleJson());

        return generateTimeWindowPredictions(operatingTime[0], operatingTime[1],
                currentTimeStr, orderMap, branchNo);
    }


    @Override
    public List<String> suggestLowActivitySlots(Long branchNo, int count) {
        return predictCongestionByTimeWindows(branchNo).entrySet().stream()
                .filter(entry -> "NORMAL".equals(entry.getValue()[1]))
                .sorted(Map.Entry.comparingByValue((a, b) -> Integer.compare((Integer) a[0], (Integer) b[0])))
                .map(Map.Entry::getKey)
                .limit(count)
                .collect(Collectors.toList());
    }
}