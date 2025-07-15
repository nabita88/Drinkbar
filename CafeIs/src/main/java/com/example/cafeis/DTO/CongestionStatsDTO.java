package com.example.cafeis.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CongestionStatsDTO {

    private LocalDateTime timeWindow;

    private Long requestTally;


    private String congestionStatus;


    public CongestionStatsDTO(LocalDateTime timeWindow, Long requestTally) {
        this.timeWindow = timeWindow;
        this.requestTally = requestTally;
    }


    public LocalDateTime getTimeInterval() {
        return this.timeWindow;
    }
}

