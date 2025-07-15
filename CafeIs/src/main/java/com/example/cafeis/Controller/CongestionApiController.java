package com.example.cafeis.Controller;

import com.example.cafeis.DTO.CongestionStatsDTO;
import com.example.cafeis.service.CongestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/congestion")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Log4j2
public class CongestionApiController {

    private final CongestionService congestionService;

    // 응답을 위한 내부 클래스들
    static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
        private String timestamp;

        public ApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
            this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }


        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
    }

    // 1. 시간별 혼잡 통계
    @GetMapping("/stats/{branchNo}")
    public ApiResponse<List<Map<String, Object>>> getStats(@PathVariable Long branchNo) {
        try {
            List<CongestionStatsDTO> stats = congestionService.getCongestionStats(branchNo);

            log.info("혼잡도 통계 조회: branchNo={}, 결과 개수={}", branchNo, stats.size());


            List<Map<String, Object>> result = new ArrayList<>();
            for (CongestionStatsDTO stat : stats) {
                Map<String, Object> item = new HashMap<>();
                item.put("timeWindow", stat.getTimeWindow() != null
                        ? stat.getTimeWindow().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        : null);
                item.put("requestTally", stat.getRequestTally() != null ? stat.getRequestTally().intValue() : 0);
                item.put("congestionStatus", stat.getCongestionStatus());
                result.add(item);
            }

            return new ApiResponse<>(true, "혼잡도 현황 조회 성공", result);
        } catch (Exception e) {
            log.error("혼잡도 통계 조회 실패: branchNo={}, error={}", branchNo, e.getMessage());
            return new ApiResponse<>(false, "혼잡도 통계 조회 실패", new ArrayList<>());
        }
    }

    // 2. 혼잡도 예측
    @GetMapping("/predict/{branchNo}")
    public ApiResponse<Map<String, Object[]>> getPredict(@PathVariable Long branchNo) {
        try {
            Map<String, Object[]> predictions = congestionService.predictCongestionByTimeWindows(branchNo);
            return new ApiResponse<>(true, "혼잡도 예측 성공", predictions);
        } catch (Exception e) {
            log.error("혼잡도 예측 실패: branchNo={}, error={}", branchNo, e.getMessage());
            return new ApiResponse<>(false, "혼잡도 예측 실패", new HashMap<>());
        }
    }

    // 3. 특정 시간 혼잡도 확인
    @GetMapping("/check/{branchNo}")
    public ApiResponse<String> checkCongestion(
            @PathVariable Long branchNo,
            @RequestParam(required = false) String targetTime) {
        try {
            LocalDateTime time = targetTime != null
                    ? LocalDateTime.parse(targetTime)
                    : LocalDateTime.now();

            String status = congestionService.getCongestionByTime(branchNo, time);
            return new ApiResponse<>(true, "혼잡도 조회 성공", status);
        } catch (Exception e) {
            log.error("혼잡도 확인 실패: branchNo={}, error={}", branchNo, e.getMessage());
            return new ApiResponse<>(false, "혼잡도 조회 실패", "NORMAL");
        }
    }

    // 4. 여유 시간대 추천
    @GetMapping("/quiet-times/{branchNo}")
    public ApiResponse<List<String>> getQuietTimes(
            @PathVariable Long branchNo,
            @RequestParam(defaultValue = "3") int count) {
        try {
            List<String> quietTimes = congestionService.suggestLowActivitySlots(branchNo, count);
            return new ApiResponse<>(true, "추천 시간 조회 성공", quietTimes);
        } catch (Exception e) {
            log.error("여유 시간대 조회 실패: branchNo={}, error={}", branchNo, e.getMessage());
            return new ApiResponse<>(false, "여유 시간대 조회 실패", new ArrayList<>());
        }
    }


    // 6. 현재 혼잡도
    @GetMapping("/current/{branchNo}")
    public ApiResponse<String> getCurrentCongestion(@PathVariable Long branchNo) {
        try {
            String status = congestionService.getCongestionByTime(branchNo, LocalDateTime.now());
            log.info("현재 혼잡도 조회: branchNo={}, status={}", branchNo, status);
            return new ApiResponse<>(true, "현재 혼잡도 조회 성공", status);
        } catch (Exception e) {
            log.error("현재 혼잡도 조회 실패: branchNo={}, error={}", branchNo, e.getMessage());
            return new ApiResponse<>(false, "현재 혼잡도 조회 실패", "NORMAL");
        }
    }
}