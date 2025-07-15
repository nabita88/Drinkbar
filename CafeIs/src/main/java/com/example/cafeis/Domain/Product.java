package com.example.cafeis.Domain;

import com.example.cafeis.Enum.ProductGroupType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_product", indexes = {
        @Index(name = "idx_product_store", columnList = "branch_no"),
        @Index(name = "idx_product_category", columnList = "category"),
        @Index(name = "idx_product_deleted", columnList = "is_deleted"),
        @Index(name = "idx_product_name", columnList = "product_name"),
        @Index(name = "idx_product_price", columnList = "selling_price"),
        @Index(name = "idx_product_branch_category", columnList = "branch_no, category"),
        @Index(name = "idx_product_branch_deleted", columnList = "branch_no, is_deleted"),
        @Index(name = "idx_product_category_deleted", columnList = "category, is_deleted"),
        @Index(name = "idx_product_branch_category_deleted", columnList = "branch_no, category, is_deleted"),
        @Index(name = "idx_product_updated", columnList = "last_updated")
})
@Getter
@ToString(exclude = "gallery")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_no")
    private Long productNo;

    @Column(name = "product_name", length = 100, nullable = false)
    private String productName;

    @Column(name = "selling_price", nullable = false)
    private int sellingPrice;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private boolean isDeleted = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 100, nullable = false)
    private ProductGroupType category;

    @Embedded
    private ProductSelectableAttribute options;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_no")
    private Branch branch;


    @Column(name = "servable_count", nullable = false)
    @Builder.Default
    private Integer servableCount = 0;

    @ElementCollection
    @BatchSize(size = 100)
    @CollectionTable(
            name = "tbl_product_visual_content",
            joinColumns = @JoinColumn(name = "product_product_no")
    )
    @OrderColumn(name = "display_rank")
    @AttributeOverrides({
            @AttributeOverride(name = "imageFileFullName", column = @Column(name = "image_file_full_name"))
    })
    @Builder.Default
    private List<ProductVisualContent> gallery = new ArrayList<>();





}

