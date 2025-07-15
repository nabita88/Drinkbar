package com.example.cafeis.DTO;


import com.example.cafeis.Enum.ProductDimensionDetail;
import com.example.cafeis.Enum.ProductThermalCondition;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;




@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddBasketItemDTO {

    @NotNull(message = "상품 번호는 반드시 입력해야 합니다.")
    private Long productNo;

    @NotNull(message = "수량은 반드시 입력해야 합니다.")
    @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
    private Integer quantity;

    private ProductDimensionDetail size;

    private ProductThermalCondition temperature;

    private String additionalOptions;

    private Integer additionalPrice;
}

