package com.example.cafeis.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "픽업 시간 확정 데이터")
public class ConfirmPickupTimeDTO {

    @Schema(description = "확정된 수령 시각", example = "2025-06-30T14:30:00", required = true)
    @NotNull(message = "픽업 시간은 반드시 지정해야 합니다")
    @Future(message = "픽업 시간은 현재보다 미래여야 합니다")
    private LocalDateTime confirmedTime;
}

