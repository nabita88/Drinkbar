package com.example.cafeis.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "카트 기반 주문 생성 데이터")
public class OrderFromBasketDTO {

    @Schema(description = "사용자 이메일 주소", example = "user0@example.com", required = true)
    private String registrantEmail;

    @Schema(description = "수령 희망 시각", example = "2024-12-25T15:30:00", required = true)
    private LocalDateTime requestedPickupTime;

    @Schema(description = "특별 요구사항 메모", example = "얼음 조금만 넣어주세요")
    private String specialRequests;
}
