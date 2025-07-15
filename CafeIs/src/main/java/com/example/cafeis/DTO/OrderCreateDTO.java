package com.example.cafeis.DTO;

import com.example.cafeis.Enum.ProductDimensionDetail;
import com.example.cafeis.Enum.ProductThermalCondition;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "신규 주문 요청 데이터")
public class OrderCreateDTO {

    @Schema(description = "사용자 이메일 주소", example = "user0@example.com")
    @NotBlank(message = "사용자 이메일은 반드시 입력해야 합니다")
    @Email(message = "올바른 이메일 주소를 입력하세요")
    private String registrantEmail;

    @Schema(description = "매장 식별번호", example = "1")
    @NotNull(message = "매장 번호는 반드시 입력해야 합니다")
    @Positive(message = "매장 번호는 양수값이어야 합니다")
    private Long branchNo;  // 매장 번호 추가

    @Schema(description = "주문 상품 목록 데이터")
    @NotEmpty(message = "주문 항목은 최소 1개 이상 필요합니다")
    @Valid
    private List<OrderItemCreateDTO> orderItems;

    @Schema(description = "수령 희망 시각", example = "2024-01-10T14:30:00")
    @NotNull(message = "픽업 시간은 반드시 지정해야 합니다")
    @Future(message = "수령 시간은 현재보다 미래여야 합니다")
    private LocalDateTime requestedPickupTime;

    @Schema(description = "특별 요구사항 메모", example = "얼음 적게 주세요")
    @Size(max = 1000, message = "특별 요청사항은 1000자를 초과할 수 없습니다")
    private String specialRequests;

    @Schema(description = "할인 적용 금액", example = "1000")
    @Min(value = 0, message = "할인 금액은 0원 이상이어야 합니다")
    private int discountAmount;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "주문 품목 입력 데이터")
    public static class OrderItemCreateDTO {
        @Schema(description = "제품 고유번호", example = "1")
        @NotNull(message = "상품 번호는 반드시 입력해야 합니다")
        @Positive(message = "상품 번호는 양수값이어야 합니다")
        private Long productNo;

        @Schema(description = "주문 수량", example = "2")
        @NotNull(message = "수량은 반드시 입력해야 합니다")
        @Min(value = 1, message = "수량은 1개 이상이어야 합니다")
        @Max(value = 100, message = "수량은 100개를 초과할 수 없습니다")
        private int quantity;

        @Schema(description = "사이즈 선택", example = "GRANDE")
        private ProductDimensionDetail size;

        @Schema(description = "온도 선택", example = "ICE")
        private ProductThermalCondition temperature;

        @Schema(description = "추가 옵션 내용", example = "샷 추가")
        @Size(max = 500, message = "추가 옵션은 500자를 초과할 수 없습니다")
        private String additionalOptions;

        @Schema(description = "추가 옵션 가격", example = "500")
        @Min(value = 0, message = "추가 금액은 0원 이상이어야 합니다")
        @Max(value = 50000, message = "추가 금액은 50,000원을 초과할 수 없습니다")
        private int additionalPrice;
    }
}

