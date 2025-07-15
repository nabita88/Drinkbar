package com.example.cafeis.DTO;

import com.example.cafeis.Enum.ProductDimensionDetail;
import com.example.cafeis.Enum.ProductGroupType;
import com.example.cafeis.Enum.ProductThermalCondition;
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
@Schema(description = "카트 상품 리스트 데이터")
public class BasketGoodsListDTO {

    @Schema(description = "카트 항목 번호", example = "1")
    private Long basketGoodsNo;

    @Schema(description = "카트 식별번호", example = "1")
    private Long basketNo;

    @Schema(description = "사용자 이메일 주소", example = "user0@example.com")
    private String email;

    @Schema(description = "매장 식별번호", example = "1")
    private Long branchNo;

    @Schema(description = "매장 명칭", example = "강남점")
    private String branchTitle;

    @Schema(description = "제품 고유번호", example = "1")
    private Long productNo;

    @Schema(description = "제품 이름", example = "아메리카노")
    private String productName;

    @Schema(description = "제품 단가", example = "4500")
    private int sellingPrice;

    @Schema(description = "제품 상세설명", example = "진한 에스프레소와 물의 조화")
    private String productDescription;

    @Schema(description = "제품 카테고리 분류", example = "COFFEE")
    private ProductGroupType productGroupType;

    @Schema(description = "제품 이미지 파일명", example = "americano.jpg")
    private String productImage;

    @Schema(description = "주문 수량", example = "2")
    private int quantity;

    @Schema(description = "선택된 사이즈 옵션", example = "GRANDE")
    private ProductDimensionDetail size;

    @Schema(description = "선택된 온도 옵션", example = "ICE")
    private ProductThermalCondition temperature;

    @Schema(description = "추가 선택 옵션", example = "샷 추가, 바닐라 시럽")
    private String additionalOptions;

    @Schema(description = "추가 옵션 가격", example = "1000")
    private int additionalPrice;

    @Schema(description = "항목별 총 가격 (상품가격 + 추가금액) * 수량", example = "11000")
    private int totalPrice;

    @Schema(description = "재고 보유 수량", example = "50")
    private int servableCount;

    @Schema(description = "재고 보유 상태", example = "true")
    private boolean hasInventory;

    @Schema(description = "제품 삭제 상태", example = "false")
    private boolean isDiscontinued;

    @Schema(description = "데이터 생성 시각", example = "2024-01-10T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "데이터 수정 시각", example = "2024-01-10T14:30:00")
    private LocalDateTime updatedAt;


    public BasketGoodsListDTO(Long basketGoodsNo, int quantity, Long productNo,
                              String productName, int sellingPrice, String productImage,
                              ProductDimensionDetail size, ProductThermalCondition temperature,
                              String additionalOptions, int additionalPrice) {
        this.basketGoodsNo = basketGoodsNo;
        this.quantity = quantity;
        this.productNo = productNo;
        this.productName = productName;
        this.sellingPrice = sellingPrice;
        this.productImage = productImage;
        this.size = size;
        this.temperature = temperature;
        this.additionalOptions = additionalOptions;
        this.additionalPrice = additionalPrice;
    }


}

