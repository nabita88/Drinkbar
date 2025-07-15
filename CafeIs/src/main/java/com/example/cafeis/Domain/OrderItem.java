package com.example.cafeis.Domain;
import com.example.cafeis.Enum.ProductThermalCondition;
import com.example.cafeis.Enum.ProductDimensionDetail;
import com.example.cafeis.Enum.OrderItemStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name = "tbl_order_item", indexes = {
        @Index(name = "idx_orderitem_order", columnList = "order_no"),
        @Index(name = "idx_orderitem_product", columnList = "product_no"),
        @Index(name = "idx_orderitem_status", columnList = "item_status"),
        @Index(name = "idx_orderitem_order_status", columnList = "order_no, item_status")
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"order", "product"})
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_no")
    private Long orderItemNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_no", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_no", nullable = false)
    private Product product;

    @Column(name = "product_name", length = 200, nullable = false)
    private String productName;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private int quantity = 1;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ProductDimensionDetail size;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ProductThermalCondition temperature;

    @Column(name = "additional_options", length = 500)
    private String additionalOptions;

    @Column(name = "additional_price")
    @Builder.Default
    private int additionalPrice = 0;

    @Column(name = "product_image", length = 300)
    private String productImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_status", length = 20, nullable = false)
    @Builder.Default
    private OrderItemStatus itemStatus = OrderItemStatus.NORMAL;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void refreshLastModifiedTime() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }


    public void setOrder(Order order) {
        this.order = order;
    }

    public int calculateFinalAmount() {

        return Optional.of(this.price)
                .map(baseAmount -> baseAmount + this.additionalPrice)
                .map(unitAmount -> unitAmount * this.quantity)
                .orElse(0);
    }

    public void extractMerchandiseDetails() {
        this.productName = product.getProductName();
        this.price = product.getSellingPrice();

        Optional.ofNullable(product.getGallery())
                .filter(gallery -> !gallery.isEmpty())
                .map(gallery -> gallery.get(0))
                .map(galleryImage -> galleryImage.getImageFileFullName())
                .ifPresent(imagePath -> this.productImage = imagePath);
    }

}

