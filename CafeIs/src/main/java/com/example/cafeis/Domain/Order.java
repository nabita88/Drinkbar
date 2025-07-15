package com.example.cafeis.Domain;

import com.example.cafeis.Enum.OrderStatus;
import com.example.cafeis.exception.BusinessException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "tbl_order", indexes = {
        @Index(name = "idx_order_registrant", columnList = "registrant_email"),
        @Index(name = "idx_order_store", columnList = "branch_no"),
        @Index(name = "idx_order_status", columnList = "order_status"),
        @Index(name = "idx_order_date", columnList = "order_date"),
        @Index(name = "idx_order_time", columnList = "order_time"),
        @Index(name = "idx_pickup_time", columnList = "confirmed_pickup_time"),
        @Index(name = "idx_order_registrant_status", columnList = "registrant_email, order_status"),
        @Index(name = "idx_order_registrant_date", columnList = "registrant_email, order_date"),
        @Index(name = "idx_order_store_status", columnList = "branch_no, order_status"),
        @Index(name = "idx_order_cash_paid", columnList = "cash_paid"),
        @Index(name = "idx_order_status_date", columnList = "order_status, order_date")
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"registrant", "branch", "orderItems"})
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_no")
    private Long orderNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrant_email", nullable = false)
    private Registrant registrant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_no", nullable = false)
    private Branch branch;

    @Column(name = "order_time", nullable = false)
    private LocalDateTime orderTime;

    @Column(name = "order_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private java.util.Date orderDate;

    @Column(name = "requested_pickup_time")
    private LocalDateTime requestedPickupTime;

    @Column(name = "confirmed_pickup_time")
    private LocalDateTime confirmedPickupTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", length = 20, nullable = false)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.PENDINGENTRY;

    @Column(name = "total_amount", nullable = false)
    @Builder.Default
    private int finalizedCost = 0;

    @Column(name = "discount_amount", nullable = false)
    @Builder.Default
    private int discountAmount = 0;

    @Column(name = "final_amount", nullable = false)
    @Builder.Default
    private int finalAmount = 0;

    @Column(name = "cash_paid", nullable = false)
    @Builder.Default
    private boolean cashPaid = false;

    @Column(name = "cash_payment_date")
    private LocalDateTime cashPaymentDate;

    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequests;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @BatchSize(size = 100)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(name = "termination_type", length = 20)
    private String terminationType;

    @Column(name = "termination_reason", length = 500)
    private String terminationReason;

    @Column(name = "termination_date")
    private LocalDateTime terminationDate;

    @Column(name = "received_date")
    private LocalDateTime receivedDate;

    @PrePersist
    public void refreshLastModifiedTime() {
        this.orderTime = LocalDateTime.now();
        this.orderDate = new java.util.Date();
        if (this.orderStatus == null) {
            this.orderStatus = OrderStatus.PENDINGENTRY;
        }
    }

    public void attachPurchaseUnit(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void adjustRequestProgress(OrderStatus status) {
        this.orderStatus = Optional.ofNullable(status)
                .orElseThrow(() -> new IllegalArgumentException("주문 상태는 필수입니다."));

        Optional.of(status)
                .filter(s -> s == OrderStatus.RECEIVED)
                .ifPresent(s -> this.receivedDate = LocalDateTime.now());
    }

    public void revokePurchaseRequest(String reason) {
        this.orderStatus = OrderStatus.TERMINATED;
        this.terminationType = "CANCEL";
        this.terminationReason = reason;
        this.terminationDate = LocalDateTime.now();
    }

    public void acknowledgeArrival() {
        this.orderStatus = Optional.of(this.orderStatus)
                .filter(status -> status == OrderStatus.READY_FOR_PICKUP)
                .map(status -> {
                    this.receivedDate = LocalDateTime.now();
                    return OrderStatus.RECEIVED;
                })
                .orElseThrow(() -> new BusinessException("INVALID_ORDER_STATUS",
                        "픽업 준비 완료된 주문만 수령 처리할 수 있습니다."));
    }


    public void calculateTotalAmount() {
        this.finalizedCost = orderItems.stream()
                .mapToInt(item -> item.calculateFinalAmount())
                .sum();
        this.finalAmount = this.finalizedCost - this.discountAmount;
    }

    public void lockPickupSchedule(LocalDateTime confirmedTime) {
        this.confirmedPickupTime = Optional.ofNullable(confirmedTime)
                .filter(time -> !time.isBefore(LocalDateTime.now()))
                .orElseThrow(() -> new BusinessException("INVALID_PICKUP_TIME",
                        confirmedTime == null ? "확정 픽업 시간은 필수입니다." : "픽업 시간은 현재 시간 이후여야 합니다."));
    }

    public void finalizeOfflinePayment() {
        this.cashPaid = true;
        this.cashPaymentDate = LocalDateTime.now();
        this.orderStatus = Optional.of(this.orderStatus)
                .filter(status -> status == OrderStatus.PENDINGENTRY)
                .map(status -> OrderStatus.LOCKEDPHASE)
                .orElse(this.orderStatus);
    }

    public void changeStatus(OrderStatus newStatus) {
        this.orderStatus = newStatus;
    }


    public int getTotalAmount() {
        return this.finalizedCost;
    }

}
