package com.example.cafeis.Enum;

import java.util.Optional;

public enum ProductThermalCondition {
    HOT("WARM"),
    COLD("ICE"),
    AMBIENT_TEMP("실온");

    private final String description;

    ProductThermalCondition(String description) {
        this.description = description;
    }

    public String getDescription() {
        return Optional.ofNullable(this.description)
                .filter(desc -> !desc.isEmpty())
                .orElse("온도 정보 없음");
    }
}
