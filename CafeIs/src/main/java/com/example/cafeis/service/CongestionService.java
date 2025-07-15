package com.example.cafeis.service;


import com.example.cafeis.DTO.CongestionStatsDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CongestionService {

    List<CongestionStatsDTO> getCongestionStats(Long branchNo);

    String calculateCongestionStatus(int requestTally, Long branchNo);

    String getCongestionByTime(Long branchNo, LocalDateTime targetTime);

    Map<String, Object[]> predictCongestionByTimeWindows(Long branchNo);

    List<String> suggestLowActivitySlots(Long branchNo, int count);

}