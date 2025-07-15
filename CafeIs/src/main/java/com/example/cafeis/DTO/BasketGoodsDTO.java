package com.example.cafeis.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "카트 항목 데이터")
public class BasketGoodsDTO {

    @Schema(description = "사용자 이메일 주소", example = "user1@test.com")
    @NotBlank(message = "이메일은 반드시 입력해야 합니다")
    @Email(message = "올바른 이메일 주소를 입력하세요")
    private String email;

    @Schema(description = "제품 고유번호", example = "1")
    @NotNull(message = "상품 번호는 반드시 입력해야 합니다")
    @Positive(message = "상품 번호는 양수값이어야 합니다")
    private Long productNo;

    @Schema(description = "주문 수량", example = "2")
    @NotNull(message = "수량은 반드시 입력해야 합니다")
    @Min(value = 1, message = "수량은 1개 이상이어야 합니다")
    @Max(value = 100, message = "수량은 100개를 초과할 수 없습니다")
    private int quantity;

    @Schema(description = "카트 항목 식별번호", example = "2")
    private Long basketGoodsNo;

    @Schema(description = "선택된 사이즈 옵션", example = "GRANDE")
    private String size;

    @Schema(description = "선택된 온도 옵션", example = "ICE")
    private String temperature;

    @Schema(description = "추가 선택사항 (샷 추가, 시럽 등)", example = "엑스트라 샷 +1, 바닐라 시럽")
    @Size(max = 500, message = "추가 옵션은 500자를 초과할 수 없습니다")
    private String additionalOptions;

    @Schema(description = "추가 옵션 가격", example = "500")
    @Min(value = 0, message = "추가 요금은 0원 이상이어야 합니다")
    @Max(value = 50000, message = "추가 요금은 50,000원을 초과할 수 없습니다")
    private int additionalPrice;

}

