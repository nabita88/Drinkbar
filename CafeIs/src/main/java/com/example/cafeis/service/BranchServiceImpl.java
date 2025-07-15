package com.example.cafeis.service;

import com.example.cafeis.DTO.BranchDTO;
import com.example.cafeis.Domain.Branch;
import com.example.cafeis.Enum.PurchasePointStatus;
import com.example.cafeis.repository.BranchRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public BranchDTO fetchBranchDetails(Long branchNo) {
        return branchRepository.findById(branchNo)
                .map(this::entityToDTO)
                .orElseThrow(() -> new IllegalArgumentException("매장을 찾을 수 없습니다: " + branchNo));
    }



    private BranchDTO entityToDTO(Branch branch) {
        BranchDTO.OperatingHoursDTO openScheduleJson = Optional.ofNullable(branch.getOpenScheduleJson())
                .flatMap(this::parseOperatingHours)
                .orElse(null);

        return BranchDTO.builder()
                .branchNo(branch.getBranchNo())
                .branchTitle(branch.getBranchTitle())
                .locationtext(BranchDTO.LocationDTO.builder()
                        .locationText(Optional.ofNullable(branch.getLocationText()).orElse(""))
                        .build())
                .seatCount(BranchDTO.SeatingInfoDTO.builder()
                        .seatCount(Optional.ofNullable(branch.getSeatCount()).orElse(0))
                        .trafficThresholds(BranchDTO.TrafficThresholdsDTO.builder()
                                .build())
                        .build())
                .openScheduleJson(openScheduleJson)
                .operatingStatus(Optional.ofNullable(branch.getOperatingStatus()).orElse(PurchasePointStatus.OPEN))
                .operationNote(Optional.ofNullable(branch.getOperationNote()).orElse(""))
                .build();
    }

    private Optional<BranchDTO.OperatingHoursDTO> parseOperatingHours(String jsonString) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, String> hoursMap = objectMapper.readValue(jsonString, Map.class);

            return Optional.of(hoursMap)
                    .filter(map -> map.containsKey("open") && map.containsKey("close"))
                    .map(map -> BranchDTO.OperatingHoursDTO.builder()
                            .open(map.get("open"))
                            .close(map.get("close"))
                            .build());
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }
}
