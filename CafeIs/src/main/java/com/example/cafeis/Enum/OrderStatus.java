package com.example.cafeis.Enum;

public enum OrderStatus {
    PENDINGENTRY("주문대기"),           // 주문 접수, 결제 대기
    LOCKEDPHASE("주문확인"),          // 결제 완료, 제조 대기중
    PREPARING("제조중"),            // 제조 진행중
    READY_FOR_PICKUP("픽업준비완료"), // 픽업 가능
    RECEIVED("수취완료"),           // 거래 완료
    TERMINATED("종료");             // 취소 또는 반품으로 종료된 주문

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

