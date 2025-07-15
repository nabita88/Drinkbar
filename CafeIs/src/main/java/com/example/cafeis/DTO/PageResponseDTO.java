package com.example.cafeis.DTO;


import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@Schema(description = "페이징 응답 데이터 - 목록 조회 결과와 페이징 메타정보를 포함하는 응답 구조체")
public class PageResponseDTO<E> {

    @Schema(
            description = "조회 결과 데이터 목록 - 현재 페이지에 포함되는 실제 데이터 배열",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<E> dtoList;

    @Schema(
            description = "페이지 번호 배열 - 현재 페이지 그룹에 표시되는 페이지 번호 목록 (최대 10개)",
            example = "[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<Integer> pageNumList;

    @Schema(
            description = "페이지 요청 데이터 - 클라이언트가 요청한 페이지 정보",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private PageRequestDTO pageRequestDTO;

    @Schema(
            description = "이전 페이지 그룹 존재 플래그 - 현재 그룹 이전에 페이지가 더 있는지 표시",
            example = "false",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean prev;

    @Schema(
            description = "다음 페이지 그룹 존재 플래그 - 현재 그룹 이후에 페이지가 더 있는지 표시",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean next;

    @Schema(
            description = "전체 데이터 수량 - 검색 조건에 맞는 전체 레코드 개수",
            example = "247",
            minimum = "0",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private int totalCount;

    @Schema(
            description = "이전 그룹 마지막 페이지 - 이전 버튼 선택 시 이동할 페이지 번호",
            example = "10",
            minimum = "0",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private int prevPage;

    @Schema(
            description = "다음 그룹 시작 페이지 - 다음 버튼 선택 시 이동할 페이지 번호",
            example = "21",
            minimum = "1",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private int nextPage;

    @Schema(
            description = "현재 그룹 페이지 개수 - 현재 표시되는 페이지 번호의 총 개수",
            example = "10",
            minimum = "1",
            maximum = "10",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private int totalPage;

    @Schema(
            description = "현재 활성 페이지 - 사용자가 현재 조회 중인 페이지 번호",
            example = "3",
            minimum = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private int current;

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(List<E> dtoList, PageRequestDTO pageRequestDTO, long totalCount) {

        this.dtoList = dtoList;
        this.pageRequestDTO = pageRequestDTO;
        this.totalCount = (int) totalCount;

        int end = (int) (Math.ceil(pageRequestDTO.getPage() / 10.0)) * 10;

        int start = end - 9;

        int last = (int) (Math.ceil((totalCount / (double) pageRequestDTO.getSize())));

        end = end > last ? last : end;

        this.prev = start > 1;

        this.next = totalCount > end * pageRequestDTO.getSize();

        this.pageNumList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());

        if (prev) {
            this.prevPage = start - 1;
        }

        if (next) {
            this.nextPage = end + 1;
        }

        this.totalPage = this.pageNumList.size();

        this.current = pageRequestDTO.getPage();

    }
}

