package com.example.cafeis.Domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"registrant", "branch"})
@Table(
        name = "tbl_basket",
        indexes = {
                @Index(name="idx_basket_email", columnList = "registrant"),
                @Index(name="idx_basket_branch", columnList = "branch_no"),
                @Index(name="idx_basket_registrant_branch", columnList = "registrant, branch_no")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_basket_registrant_store", columnNames = {"registrant", "branch_no"})
        }
)
public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "basket_no")
    private Long basketNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="registrant", nullable = false)
    private Registrant registrant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_no", nullable = false)
    private Branch branch;



    @OneToMany(mappedBy = "basket", cascade = CascadeType.ALL, orphanRemoval = true)

    @Builder.Default
    @BatchSize(size = 100)
    private List<BasketGoods> basketGoodsPerson  = new ArrayList<>();




    public void addComponent(Product product, int quantity) {
        Optional.of(basketGoodsPerson.stream()
                .filter(component -> component.getProduct().getProductNo().equals(product.getProductNo()))
                .findFirst()
                .map(existingComponent -> {
                    existingComponent.changeQuantity(existingComponent.getQuantity() + quantity);
                    return existingComponent;
                })
                .orElseGet(() -> {
                    BasketGoods newComponent = BasketGoods.builder()
                            .basket(this)
                            .product(product)
                            .quantity(quantity)
                            .build();
                    basketGoodsPerson.add(newComponent);
                    return newComponent;
                })
        );
    }


    public void clear() {
        basketGoodsPerson.clear();
    }


}