package com.example.cafeis.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
@Schema(description = "카트 데이터")
public class BasketDTO {

    @Schema(description = "카트 식별번호", example = "1")
    private Long basketNo;

    @Schema(description = "사용자 이메일 주소", example = "user0@example.com")
    @NotBlank(message = "사용자 이메일은 반드시 입력해야 합니다")
    @Email(message = "올바른 이메일 주소를 입력하세요")
    private String registrantEmail;

    @Schema(description = "사용자 성명", example = "홍길동")
    private String registrantName;

    @Schema(description = "매장 식별번호", example = "1")
    @NotNull(message = "매장 번호는 반드시 입력해야 합니다")
    @Positive(message = "매장 번호는 양수값이어야 합니다")
    private Long branchNo;

    @Schema(description = "매장 명칭", example = "강남점")
    private String branchTitle;

    @Schema(description = "매장 소재지", example = "서울시 강남구 테헤란로 123")
    private String storeAddress;

    @Schema(description = "카트 상품 목록 데이터")
    private List<BasketGoodsListDTO> items;

    @Schema(description = "전체 상품 수량", example = "5")
    private int totalItemCount;

    @Schema(description = "전체 상품 종류 수", example = "3")
    private int totalProductCount;

    @Schema(description = "전체 상품 가격", example = "25000")
    private int finalizedCost;

    @Schema(description = "예상 추가 옵션 가격", example = "2000")
    private int totalAdditionalPrice;

    @Schema(description = "예상 최종 결제 가격", example = "27000")
    private int estimatedFinalAmount;

    @Schema(description = "데이터 생성 시각", example = "2024-01-10T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "데이터 수정 시각", example = "2024-01-10T14:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "주문 불가능 사유", example = "재고 부족 상품이 있습니다")
    private String cannotOrderReason;

    public void calculateTotals() {
        if (items != null) {
            this.totalProductCount = items.size();
            this.totalItemCount = items.stream()
                    .mapToInt(BasketGoodsListDTO::getQuantity)
                    .sum();
            this.finalizedCost = items.stream()
                    .mapToInt(item -> item.getSellingPrice() * item.getQuantity())
                    .sum();
            this.totalAdditionalPrice = items.stream()
                    .mapToInt(item -> item.getAdditionalPrice() * item.getQuantity())
                    .sum();
            this.estimatedFinalAmount = this.finalizedCost + this.totalAdditionalPrice;

        }
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
}

