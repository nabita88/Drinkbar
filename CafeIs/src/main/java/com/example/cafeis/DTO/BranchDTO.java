package com.example.cafeis.DTO;

import com.example.cafeis.Enum.PurchasePointStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
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
@Schema(description = "점포 데이터")
public class BranchDTO {

    @Schema(description = "점포 번호", example = "1")
    private Long branchNo;

    @Schema(description = "점포명", example = "강남점")
    @NotBlank(message = "매장명은 반드시 입력해야 합니다")
    @Size(min = 2, max = 100, message = "매장명은 최소 2자, 최대 100자로 입력하세요")
    private String branchTitle;

    @Schema(description = "매장 위치 데이터")
    private LocationDTO locationtext;

    @Schema(description = "수용 능력 데이터")
    private SeatingInfoDTO seatCount;

    @Schema(description = "운영 시간 데이터")
    private OperatingHoursDTO openScheduleJson;

    @Schema(description = "운영 상태 코드", example = "OPEN")
    private PurchasePointStatus operatingStatus;

    @Schema(description = "상태 안내 메시지", example = "정상 영업 중")
    @Size(max = 200, message = "상태 메시지는 200자를 초과할 수 없습니다")
    private String operationNote;


    // 내부 DTO 클래스들
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "매장 위치 데이터")
    public static class LocationDTO {
        @Schema(description = "매장 주소지", example = "서울시 강남구 테헤란로 123")
        @NotBlank(message = "주소는 반드시 입력해야 합니다")
        @Size(max = 200, message = "주소는 200자를 초과할 수 없습니다")
        private String locationText;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "수용 능력 데이터")
    public static class SeatingInfoDTO {
        @Schema(description = "전체 좌석 수량", example = "50")
        @Min(value = 0, message = "좌석 수는 0개 이상이어야 합니다")
        private Integer seatCount;

        @Schema(description = "혼잡도 임계값 설정")
        private TrafficThresholdsDTO trafficThresholds;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "혼잡도 임계값 설정")
    public static class TrafficThresholdsDTO {
        @Schema(description = "혼잡도 임계값 - 낮음 단계", example = "20")
        @Min(value = 0, message = "임계값은 0 이상이어야 합니다")
        private Integer low;

        @Schema(description = "혼잡도 임계값 - 중간 단계", example = "35")
        @Min(value = 0, message = "임계값은 0 이상이어야 합니다")
        private Integer medium;

        @Schema(description = "혼잡도 임계값 - 높음 단계", example = "45")
        @Min(value = 0, message = "임계값은 0 이상이어야 합니다")
        private Integer high;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "운영 시간 데이터")
    public static class OperatingHoursDTO {
        @Schema(description = "영업 시작 시각", example = "08:00")
        private String open;

        @Schema(description = "영업 종료 시각", example = "22:00")
        private String close;
    }
}
