package com.example.cafeis.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "오류 응답 데이터")
public class ErrorResponseDTO {

    @Schema(description = "오류 코드 번호", example = "400")
    private String code;

    @Schema(description = "오류 설명 메시지", example = "잘못된 요청입니다.")
    private String message;

    @Schema(description = "오류 발생 시각", example = "2024-01-01T10:00:00")
    private String timestamp;
}
