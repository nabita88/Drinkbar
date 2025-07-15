package com.example.cafeis.Enum;

import lombok.Getter;

@Getter
public enum TimeOrderEnd {
    IMMEDIATE(0, 5, "즉시 제조"),
    FIVE_MIN_LATER(5, 10, "5분 후 제조"),
    TEN_MIN_LATER(10, 15, "10분 후 제조");

    private final int startMinutes;
    private final int endMinutes;
    private final String description;

    TimeOrderEnd(int startMinutes, int endMinutes, String description) {
        this.startMinutes = startMinutes;
        this.endMinutes = endMinutes;
        this.description = description;
    }
}

