package com.example.cafeis.Enum;

import java.util.Arrays;
import java.util.Optional;

public enum PurchasePointStatus {
    OPEN("영업중"),
    CLOSED("영업종료"),
    MAINTENANCE("점검중");



    private final String displayText;

    PurchasePointStatus(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }
    // Stream API를 활용한 추가 메서드
    public static Optional<PurchasePointStatus> fromDisplayText(String text) {
        return Arrays.stream(values())
                .filter(status -> status.displayText.equals(text))
                .findFirst();
    }

    public boolean isOperational() {
        return this == OPEN;
    }

}

