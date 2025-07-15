package com.example.cafeis.DTO;

import com.example.cafeis.Domain.ProductVisualContent;
import com.example.cafeis.Enum.ProductGroupType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "제품 데이터 모델")
public class ProductDTO {

    @Schema(description = "제품 고유번호", example = "1")
    private Long productNo;

    @Schema(description = "제품 이름", example = "아메리카노")
    @NotBlank(message = "제품명은 반드시 입력해야 합니다")
    @Size(min = 2, max = 100, message = "제품명은 최소 2자, 최대 100자로 입력하세요")
    private String productName;

    @Schema(description = "판매 가격", example = "4500")
    @NotNull(message = "가격은 반드시 입력해야 합니다")
    @Positive(message = "가격은 양수값이어야 합니다")
    @Max(value = 1000000, message = "가격은 1,000,000원을 넘을 수 없습니다")
    private Integer sellingPrice;

    @Schema(description = "제품 상세설명", example = "진한 에스프레소와 물의 조화")
    @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
    private String description;

    @Schema(description = "삭제 상태", example = "false")
    private boolean isDeleted;

    @Schema(description = "제품 카테고리", example = "COFFEE")
    @NotNull(message = "카테고리는 반드시 선택해야 합니다")
    private ProductGroupType category;

    @Schema(description = "제품 옵션 정보")
    private ProductOptionDTO options;

    @Schema(description = "매장 식별번호", example = "1")
    @NotNull(message = "매장 번호는 반드시 입력해야 합니다")
    private Long branchNo;

    @Schema(description = "재고 보유량", example = "100")
    @NotNull(message = "재고 수량은 반드시 입력해야 합니다")
    @Min(value = 0, message = "재고는 0개 이상이어야 합니다")
    private Integer servableCount;

    @Schema(description = "제품 이미지 목록")
    private List<ProductVisualContent> gallery;

    @Schema(description = "최종 수정 일시", example = "2024-01-10T14:30:00")
    private LocalDateTime lastUpdated;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "제품 옵션 데이터")
    public static class ProductOptionDTO {

        @Schema(description = "선택 가능한 사이즈 옵션", example = "[\"TALL\", \"GRANDE\", \"VENTI\"]")
        private Set<String> sizes;

        @Schema(description = "선택 가능한 온도 옵션", example = "[\"HOT\", \"ICE\"]")
        private Set<String> temperatures;
    }


    @Builder.Default
    private List<MultipartFile> files = new ArrayList<>();

    @Builder.Default
    private List<String> uploaded_visual_identifiers = new ArrayList<>();

}
