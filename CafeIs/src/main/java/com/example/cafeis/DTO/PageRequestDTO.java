package com.example.cafeis.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "페이징 요청 데이터 - 리스트 조회용 페이지 정보를 설정합니다")
public class PageRequestDTO {

    @Schema(
            description = "현재 페이지 - 1번부터 시작되는 페이지 인덱스",
            example = "1",
            minimum = "1",
            maximum = "1000",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @Builder.Default
    private int page = 1;

    @Schema(
            description = "페이지당 데이터 개수 - 1~100개 범위에서 지정",
            example = "10",
            minimum = "1",
            maximum = "100",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @Builder.Default
    private int size = 10;
}
