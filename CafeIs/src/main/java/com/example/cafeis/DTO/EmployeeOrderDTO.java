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
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeOrderDTO {
    private Long orderNo;
    private String registrantEmail;
    private String customerName;
    private LocalDateTime orderTime;
    private LocalDateTime requestedPickupTime;
    private LocalDateTime confirmedPickupTime;
    private Integer finalAmount;
    private Long itemCount;
    private OrderStatus orderStatus;
    private String specialRequests;
    private List<OrderItemDTO> items;
    private Integer remainingMinutes;
    private String priority;

}
