package com.example.cafeis.DTO;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "표준 API 응답 형식 - 전체 API에서 공통으로 사용되는 응답 래퍼")
public class ApiResponseDTO<T> {

    @Schema(
            description = "처리 결과 상태 - true: 정상 처리, false: 처리 실패",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean success;

    @Schema(
            description = "결과 데이터 - 요청 성공 시 반환할 실제 정보",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private T data;

    @Schema(
            description = "처리 성공 메시지 - 정상 처리 시 제공되는 안내 메시지",
            example = "요청이 성공적으로 처리되었습니다.",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String message;

    @Schema(
            description = "오류 상세 정보 - 처리 실패 시 원인과 해결 방안 설명",
            example = "잘못된 요청입니다. 필수 파라미터를 확인해주세요.",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String error;


    public static <T> ApiResponseDTO<T> success(T data) {
        return ApiResponseDTO.<T>builder()
                .success(true)
                .data(data)
                .build();
    }


    public static <T> ApiResponseDTO<T> success(T data, String message) {
        return ApiResponseDTO.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .build();
    }


    public static <T> ApiResponseDTO<T> error(String error) {
        return ApiResponseDTO.<T>builder()
                .success(false)
                .error(error)
                .build();
    }
}