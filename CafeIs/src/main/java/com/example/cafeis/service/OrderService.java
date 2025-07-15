package com.example.cafeis.service;

import com.example.cafeis.DTO.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    Page<OrderListDTO> queryByProgressMarker(String email, Pageable pageable);

    Long registerPurchaseRequest(OrderCreateDTO orderCreateDTO);

    OrderDTO fetchRequestDetails(Long orderNo);

    void adjustRequestProgress(OrderStatusUpdateDTO updateDTO);

    void finalizeOfflinePayment(Long orderNo);

    void revokePurchaseRequest(Long orderNo, String reason);

    void acknowledgeArrival(Long orderNo);

    void lockPickupSchedule(Long orderNo, LocalDateTime confirmedTime);

    List<OrderListDTO> listScheduledPickupsByDate(LocalDateTime date);

    List<EmployeeOrderDTO> getOrdersByTimeWindow(int startMinutes, int endMinutes);

    Long createOrderFromBasket(String registrantEmail, LocalDateTime requestedPickupTime, String specialRequests);

}

