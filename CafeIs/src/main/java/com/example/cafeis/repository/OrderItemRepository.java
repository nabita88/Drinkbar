package com.example.cafeis.repository;

import com.example.cafeis.Domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {


    //특정 주문의 아이템들 조회
//주문 상세 정보 조회 시 사용
    @Query("SELECT oi " +
            "FROM OrderItem oi " +
            "WHERE oi.order.orderNo = :orderNo " +
            "ORDER BY oi.orderItemNo ASC")
    List<OrderItem> findByOrderNo(@Param("orderNo") Long orderNo);


//특정 상품의 주문 아이템들 조회
//상품별 주문 내역

    @Query("SELECT oi " +
            "FROM OrderItem oi " +
            "WHERE oi.product.productNo = :productNo " +
            "ORDER BY oi.order.orderTime DESC")
    List<OrderItem> findByProductNo(@Param("productNo") Long productNo);
}

