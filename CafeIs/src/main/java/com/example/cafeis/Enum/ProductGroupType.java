package com.example.cafeis.Enum;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public enum ProductGroupType {
    COFFEE("커피"),
    TEA("차"),
    DESSERT("디저트"),
    BAKERY("베이커리"),
    BEVERAGE("음료"),
    FOOD("푸드");
    private final String description;
    ProductGroupType(String description) {
        this.description = description;
    }
    public String getDescription() {
        return Optional.ofNullable(this.description)
                .filter(desc -> !desc.isEmpty())
                .orElse("미분류");
    }

    public static Optional<ProductGroupType> fromDescription(String description) {
        return Arrays.stream(values())
                .filter(type -> type.getDescription().equals(description))
                .findFirst();
    }

    public static Optional<ProductGroupType> fromName(String name) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(name))
                .findFirst();
    }

    public static Stream<String> getAllDescriptions() {
        return Arrays.stream(values())
                .map(ProductGroupType::getDescription);
    }

    @Override
    public String toString() {
        return this.description;
    }
}
