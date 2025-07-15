package com.example.cafeis.Enum;


import java.util.Optional;

public enum ProductDimensionDetail {

    LARGE("라지"),
    MEGA("메가"),

    ;

    private final String description;

    ProductDimensionDetail(String description) {
        this.description = description;
    }

    public String getDescription() {
        return Optional.ofNullable(this.description)
                .filter(desc -> !desc.isEmpty())
                .orElse("기본 사이즈");
    }


}
