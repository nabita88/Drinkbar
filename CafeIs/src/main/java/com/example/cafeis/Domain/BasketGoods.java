package com.example.cafeis.Domain;
import com.example.cafeis.Enum.ProductThermalCondition;
import com.example.cafeis.Enum.ProductDimensionDetail;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_basket_goods",
        indexes = {
                @Index(name = "idx_basket_goods", columnList = "product_no, basket_no"),
                @Index(name = "idx_basket_goods_created", columnList = "created_at"),
                @Index(name = "idx_basket_goods_basket", columnList = "basket_no")
        })
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"basket", "product"})
public class BasketGoods {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "basket_goods_no")
    private Long basketGoodsNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basket_no", nullable = false)
    private Basket basket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_no", nullable = false)
    private Product product;

    @Column(nullable = false)
    @Builder.Default
    private int quantity = 1;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ProductDimensionDetail size;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ProductThermalCondition temperature;

    @Column(length = 500)
    private String additionalOptions;

    @Column(name = "additional_price")
    @Builder.Default
    private int additionalPrice = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    public void changeQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        if (quantity > 100) {
            throw new IllegalArgumentException("수량은 100개를 초과할 수 없습니다.");
        }
        this.quantity = quantity;

    }


}

