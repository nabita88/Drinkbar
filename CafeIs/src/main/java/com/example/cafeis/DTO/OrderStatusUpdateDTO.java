package com.example.cafeis.DTO;

import com.example.cafeis.Enum.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusUpdateDTO {

    private Long orderNo;
    private OrderStatus orderStatus;
    private OrderStatus newStatus;
    private LocalDateTime confirmedPickupTime;
    private String cancelReason;
    private String returnReason;
}

