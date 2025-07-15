package com.example.cafeis.DTO;

import com.example.cafeis.Enum.OrderStatus;
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
public class OrderListDTO {

    private Long orderNo;
    private String registrantEmail;
    private LocalDateTime orderTime;
    private OrderStatus orderStatus;
    private int finalAmount;
    private int totalAmount;
    private String branchName;
    private boolean cashPaid;
    private LocalDateTime requestedPickupTime;
    private LocalDateTime confirmedPickupTime;
    private Long itemCount; // 주문 상품 개수
    private List<OrderItemDTO> items;

    // JPQL 쿼리용 생성자 추가
    public OrderListDTO(Long orderNo, String registrantEmail, LocalDateTime orderTime,
                        OrderStatus orderStatus, int finalAmount, boolean cashPaid,
                        LocalDateTime requestedPickupTime, LocalDateTime confirmedPickupTime,
                        Long itemCount) {
        this.orderNo = orderNo;
        this.registrantEmail = registrantEmail;
        this.orderTime = orderTime;
        this.orderStatus = orderStatus;
        this.finalAmount = finalAmount;
        this.totalAmount = finalAmount;
        this.cashPaid = cashPaid;
        this.requestedPickupTime = requestedPickupTime;
        this.confirmedPickupTime = confirmedPickupTime;
        this.itemCount = itemCount;
    }
}
