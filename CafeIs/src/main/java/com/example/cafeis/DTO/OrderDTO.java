package com.example.cafeis.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "주문 정보 응답 데이터")
public class OrderDTO {

    @Schema(description = "주문 식별번호", example = "1")
    private Long orderNo;

    @Schema(description = "사용자 이메일 주소", example = "user0@example.com")
    private String registrantEmail;

    @Schema(description = "사용자 성명", example = "홍길동")
    private String registrantName;

    @Schema(description = "매장 식별번호", example = "1")
    private Long branchNo;

    @Schema(description = "매장 명칭", example = "강남점")
    private String branchTitle;

    @Schema(description = "매장명", example = "강남점")
    private String branchName;

    @Schema(description = "매장 소재지", example = "서울시 강남구 테헤란로 123")
    private String storeAddress;

    @Schema(description = "주문 일시", example = "2024-01-10T10:30:00")
    private LocalDateTime orderTime;

    @Schema(description = "주문 날짜 정보", example = "2024-01-10")
    private java.util.Date orderDate;

    @Schema(description = "요청 수령 시각", example = "2024-01-10T11:00:00")
    private LocalDateTime requestedPickupTime;

    @Schema(description = "확정된 수령 시각", example = "2024-01-10T11:00:00")
    private LocalDateTime confirmedPickupTime;

    @Schema(description = "주문 처리 상태", example = "LOCKEDPHASE")
    private String orderStatus;

    @Schema(description = "주문 상태 설명 텍스트", example = "주문 확정")
    private String orderStatusDescription;

    @Schema(description = "전체 주문 가격", example = "25000")
    private int finalizedCost;

    @Schema(description = "총 주문 금액", example = "25000")
    private int totalAmount;

    @Schema(description = "할인 적용 금액", example = "2500")
    private int discountAmount;

    @Schema(description = "최종 결제 가격", example = "22500")
    private int finalAmount;

    @Schema(description = "현금 결제 완료 여부", example = "true")
    private boolean cashPaid;

    @Schema(description = "현금 결제 처리 시각", example = "2024-01-10T10:35:00")
    private LocalDateTime cashPaymentDate;

    @Schema(description = "특별 요청 내용", example = "얼음 적게 주세요")
    private String specialRequests;

    @Schema(description = "종료 유형 코드 (CANCEL)", example = "CANCEL")
    private String terminationType;

    @Schema(description = "종료 사유 설명", example = "고객 요청")
    private String terminationReason;

    @Schema(description = "종료 처리 시각", example = "2024-01-10T11:30:00")
    private LocalDateTime terminationDate;

    @Schema(description = "수령 완료 시각", example = "2024-01-10T11:05:00")
    private LocalDateTime receivedDate;

    @Schema(description = "주문 상품 목록 데이터")
    private List<OrderItemDTO> orderItems;

    @Schema(description = "주문 상품 목록")
    private List<OrderItemDTO> items;

    @Schema(description = "주문 상품 종류 수", example = "3")
    private int orderItemCount;

    @Schema(description = "전체 상품 수량 합계", example = "5")
    private int totalQuantity;


}

