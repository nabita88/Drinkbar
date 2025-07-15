package com.example.cafeis.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "주문 상품 데이터")
public class OrderItemDTO {

    @Schema(description = "주문 항목 식별번호", example = "1")
    private Long orderItemNo;

    @Schema(description = "제품 고유번호", example = "1")
    private Long productNo;

    @Schema(description = "제품 이름", example = "아메리카노")
    private String productName;

    @Schema(description = "제품 단가", example = "4500")
    private int price;

    @Schema(description = "주문 수량", example = "2")
    private int quantity;

    @Schema(description = "사이즈 선택", example = "GRANDE")
    private String size;

    @Schema(description = "온도 선택", example = "ICE")
    private String temperature;

    @Schema(description = "추가 선택사항", example = "샷 추가, 바닐라 시럽")
    private String additionalOptions;

    @Schema(description = "추가 옵션 가격", example = "1000")
    private int additionalPrice;

    @Schema(description = "제품 이미지 파일명", example = "product1.jpg")
    private String productImage;

    @Schema(description = "항목 처리 상태", example = "NORMAL")
    private String itemStatus;

    @Schema(description = "항목별 총 가격 (단가 + 추가금액) * 수량", example = "11000")
    private int totalPrice;
}

