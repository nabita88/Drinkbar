package com.example.cafeis.DTO;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "페이지 결과")
public class PageResult<T> {

    @Schema(description = "데이터 목록")
    private List<T> dtoList;

    @Schema(description = "총 개수", example = "100")
    private long totalCount;

    @Schema(description = "현재 페이지", example = "1")
    private int page;

    @Schema(description = "페이지 크기", example = "10")
    private int size;

    @Schema(description = "시작 페이지", example = "1")
    private int start;

    @Schema(description = "끝 페이지", example = "10")
    private int end;

    @Schema(description = "이전 페이지 존재 여부", example = "false")
    private boolean prev;

    @Schema(description = "다음 페이지 존재 여부", example = "true")
    private boolean next;
}
