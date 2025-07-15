package com.example.cafeis.Domain;

import com.example.cafeis.Enum.ProductDimensionDetail;
import com.example.cafeis.Enum.ProductThermalCondition;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;



@Embeddable
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductSelectableAttribute {


    @Column(name = "dimension_large")
    @Builder.Default
    private Boolean dimensionLarge = false;

    @Column(name = "dimension_mega")
    @Builder.Default
    private Boolean dimensionMega = false;


    @Column(name = "thermal_cold")
    @Builder.Default
    private Boolean thermalCold = false;

    @Column(name = "thermal_hot")
    @Builder.Default
    private Boolean thermalHot = false;

    // 편의 메서드들
    public boolean canServeSize(ProductDimensionDetail size) {
        return switch (size) {
            case LARGE -> Boolean.TRUE.equals(dimensionLarge);
            case MEGA -> Boolean.TRUE.equals(dimensionMega);

            default -> false;
        };
    }

    public boolean canServeTemperature(ProductThermalCondition temp) {
        return switch (temp) {
            case COLD -> Boolean.TRUE.equals(thermalCold);
            case HOT -> Boolean.TRUE.equals(thermalHot);
            default -> false;
        };
    }
}
