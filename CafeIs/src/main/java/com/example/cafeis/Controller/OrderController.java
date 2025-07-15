package com.example.cafeis.Controller;

import com.example.cafeis.DTO.*;
import com.example.cafeis.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Tag(name = "Admin Order", description = "어드민 전용 주문 처리 API")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "카트 기반 주문 생성 API", description = "장바구니의 전체 상품으로 주문을 등록합니다", tags = {"Order"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ApiResponseDTO.class)), description = "주문 등록 완료"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ApiResponseDTO.class)), description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = ApiResponseDTO.class)), description = "접근 권한 없음"),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiResponseDTO.class)), description = "장바구니 미존재")})
    @PostMapping("/from-basket-order")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> createOrderFromBasket(
            @RequestParam(value = "email", defaultValue = "user0@example.com") String email,
            @Valid @RequestBody OrderFromBasketDTO orderFromBasketDTO) {

        log.info("장바구니에서 주문 생성 중 - 회원: {}", email);

        Long orderNo = orderService.createOrderFromBasket(
                email,
                orderFromBasketDTO.getRequestedPickupTime(),
                orderFromBasketDTO.getSpecialRequests()
        );

        return ResponseEntity.ok(ApiResponseDTO.<Map<String, Object>>builder()
                .success(true)
                .message("장바구니에서 주문이 성공적으로 생성되었습니다.")
                .data(Map.of("orderNo", orderNo))
                .build());
    }

    // 주문 조회 기능들
    @Operation(summary = "회원 주문 목록 조회", description = "로그인한 회원의 주문 목록을 페이지네이션으로 조회합니다", tags = {"Order"})
    @GetMapping("/my-orders")
    public ResponseEntity<Page<OrderListDTO>> getMyOrders(
            @RequestParam(value = "email", defaultValue = "user0@example.com") String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("회원 주문 목록 조회 - 이메일: {}, 페이지: {}, 사이즈: {}", email, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderListDTO> orders = orderService.queryByProgressMarker(email, pageable);

        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "주문 상세 조회", description = "특정 주문의 상세 정보를 조회합니다", tags = {"Order"})
    @GetMapping("/{orderNo}")
    public ResponseEntity<ApiResponseDTO<OrderDTO>> getOrderDetail(@PathVariable Long orderNo) {

        log.info("주문 상세 조회 - 주문번호: {}", orderNo);

        try {
            OrderDTO order = orderService.fetchRequestDetails(orderNo);
            return ResponseEntity.ok(ApiResponseDTO.<OrderDTO>builder()
                    .success(true)
                    .message("주문 상세 정보")
                    .data(order)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.<OrderDTO>builder()
                            .success(false)
                            .message("주문을 찾을 수 없습니다.")
                            .build());
        }
    }

    // 주문 상태 관리
    @Operation(summary = "주문 상태 변경", description = "주문의 상태를 변경합니다 (직원용)", tags = {"Admin Order"})
    @PutMapping("/{orderNo}/status")
    public ResponseEntity<ApiResponseDTO<String>> updateOrderStatus(
            @PathVariable Long orderNo,
            @Valid @RequestBody OrderStatusUpdateDTO updateDTO) {

        log.info("주문 상태 변경 - 주문번호: {}, 새 상태: {}", orderNo, updateDTO.getOrderStatus());

        updateDTO.setOrderNo(orderNo);
        orderService.adjustRequestProgress(updateDTO);

        return ResponseEntity.ok(ApiResponseDTO.<String>builder()
                .success(true)
                .message("주문 상태가 변경되었습니다.")
                .data("SUCCESS")
                .build());
    }

    @Operation(summary = "픽업 시간 확정", description = "주문의 픽업 시간을 확정합니다 (직원용)", tags = {"Admin Order"})
    @PutMapping("/{orderNo}/pickup-time")
    public ResponseEntity<ApiResponseDTO<String>> confirmPickupTime(
            @PathVariable Long orderNo,
            @RequestParam @Parameter(description = "확정된 픽업 시간") LocalDateTime confirmedTime) {

        log.info("픽업 시간 확정 - 주문번호: {}, 확정시간: {}", orderNo, confirmedTime);

        orderService.lockPickupSchedule(orderNo, confirmedTime);

        return ResponseEntity.ok(ApiResponseDTO.<String>builder()
                .success(true)
                .message("픽업 시간이 확정되었습니다.")
                .data("SUCCESS")
                .build());
    }

    @Operation(summary = "주문 수령 확인", description = "고객이 주문을 수령했음을 확인합니다", tags = {"Order"})
    @PutMapping("/{orderNo}/acknowledge")
    public ResponseEntity<ApiResponseDTO<String>> acknowledgeOrder(@PathVariable Long orderNo) {

        log.info("주문 수령 확인 - 주문번호: {}", orderNo);

        orderService.acknowledgeArrival(orderNo);

        return ResponseEntity.ok(ApiResponseDTO.<String>builder()
                .success(true)
                .message("주문 수령이 확인되었습니다.")
                .data("SUCCESS")
                .build());
    }

    // 결제 관리
    @Operation(summary = "오프라인 결제 완료", description = "현금 결제를 완료 처리합니다", tags = {"Admin Order"})
    @PutMapping("/{orderNo}/offline-payment")
    public ResponseEntity<ApiResponseDTO<String>> completeOfflinePayment(@PathVariable Long orderNo) {

        log.info("오프라인 결제 완료 처리 - 주문번호: {}", orderNo);

        orderService.finalizeOfflinePayment(orderNo);

        return ResponseEntity.ok(ApiResponseDTO.<String>builder()
                .success(true)
                .message("현금 결제가 완료되었습니다.")
                .data("SUCCESS")
                .build());
    }

    //  취소 관리
    @Operation(summary = "주문 취소", description = "주문을 취소합니다", tags = {"Order"})
    @DeleteMapping("/{orderNo}")
    public ResponseEntity<ApiResponseDTO<String>> cancelOrder(
            @PathVariable Long orderNo,
            @RequestParam(required = false, defaultValue = "고객 요청") String reason) {

        log.info("주문 취소 - 주문번호: {}, 사유: {}", orderNo, reason);

        orderService.revokePurchaseRequest(orderNo, reason);

        return ResponseEntity.ok(ApiResponseDTO.<String>builder()
                .success(true)
                .message("주문이 취소되었습니다.")
                .data("SUCCESS")
                .build());
    }

    //직원용 기능
    @Operation(summary = "날짜별 픽업 예정 목록", description = "특정 날짜의 픽업 예정 주문들을 조회합니다", tags = {"Admin Order"})
    @GetMapping("/pickup-schedule")
    public ResponseEntity<ApiResponseDTO<List<OrderListDTO>>> getPickupSchedule(
            @RequestParam @Parameter(description = "조회할 날짜") LocalDateTime date) {

        log.info("날짜별 픽업 예정 목록 조회 - 날짜: {}", date);

        List<OrderListDTO> orders = orderService.listScheduledPickupsByDate(date);

        return ResponseEntity.ok(ApiResponseDTO.<List<OrderListDTO>>builder()
                .success(true)
                .message("픽업 예정 주문 목록")
                .data(orders)
                .build());
    }

    @Operation(summary = "시간대별 주문 조회", description = "지정된 시간 범위의 주문들을 조회합니다 (대시보드용)", tags = {"Admin Order"})
    @GetMapping("/time-window")
    public ResponseEntity<ApiResponseDTO<List<EmployeeOrderDTO>>> getOrdersByTimeWindow(
            @RequestParam(defaultValue = "0") int startMinutes,
            @RequestParam(defaultValue = "60") int endMinutes) {

        log.info("시간대별 주문 조회 - 시작: {}분 후, 종료: {}분 후", startMinutes, endMinutes);

        List<EmployeeOrderDTO> orders = orderService.getOrdersByTimeWindow(startMinutes, endMinutes);

        return ResponseEntity.ok(ApiResponseDTO.<List<EmployeeOrderDTO>>builder()
                .success(true)
                .message("시간대별 주문 목록")
                .data(orders)
                .build());
    }








}
