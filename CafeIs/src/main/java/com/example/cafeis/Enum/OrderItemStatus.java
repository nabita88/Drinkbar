package com.example.cafeis.Enum;

public enum OrderItemStatus {
    NORMAL("정상"),              // 정상
    CANCELLED("취소"),           // 개별 상품 취소
    RETURNED("반품"),            // 개별 상품 반품
    EXCHANGED("교환");           // 개별 상품 교환

    private final String description;

    OrderItemStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
