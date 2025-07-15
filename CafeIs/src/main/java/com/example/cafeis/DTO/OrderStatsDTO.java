package com.example.cafeis.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatsDTO {
    private Long requestTally;
    private Long finalizedCost;
    private LocalDateTime date;
}
