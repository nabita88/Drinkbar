package com.example.cafeis.DTO;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "제품 목록 조회 응답 데이터 - 페이징 및 필터링된 제품 목록 정보를 전달합니다")
public class ProductListResponseDTO {

    @Schema(
            description = "처리 결과 상태 - true: 정상 처리, false: 처리 실패",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean success;

    @Schema(
            description = "제품 목록 및 관련 메타정보를 포함하는 응답 데이터",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ProductDataDTO data;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "제품 목록 데이터 및 페이징 정보 구조체")
    public static class ProductDataDTO {

        @Schema(
                description = "조회된 제품 상세 정보 목록 - 각 제품의 속성과 이미지, 가격 정보 포함",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private List<ProductDTO> products;

        @Schema(
                description = "검색 조건 충족 제품의 전체 개수 - 페이지 처리용 메타데이터",
                example = "147",
                minimum = "0",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private Long totalCount;
    }


    public static ProductListResponseDTO success(List<ProductDTO> products, int totalCount) {
        ProductDataDTO data = ProductDataDTO.builder()
                .products(products)
                .totalCount((long) totalCount)
                .build();

        return ProductListResponseDTO.builder()
                .success(true)
                .data(data)
                .build();
    }
}
