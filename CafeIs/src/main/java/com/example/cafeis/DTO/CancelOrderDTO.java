package com.example.cafeis.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "주문 취소 요청 데이터")
public class CancelOrderDTO {

    @Schema(description = "취소 사유 내용", example = "고객 요청으로 인한 주문 취소", required = true)
    @NotBlank(message = "취소 사유는 반드시 입력해야 합니다")
    @Size(min = 5, max = 500, message = "취소 사유는 최소 5자, 최대 500자로 작성하세요")
    private String reason;
}
