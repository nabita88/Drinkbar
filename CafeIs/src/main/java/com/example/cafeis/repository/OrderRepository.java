package com.example.cafeis.repository;

import com.example.cafeis.DTO.CongestionStatsDTO;
import com.example.cafeis.DTO.OrderListDTO;
import com.example.cafeis.Domain.Order;
import com.example.cafeis.Enum.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value = "SELECT DISTINCT o " +
            "FROM Order o " +
            "LEFT JOIN FETCH o.orderItems oi " +
            "LEFT JOIN FETCH oi.product " +
            "LEFT JOIN FETCH o.branch " +
            "LEFT JOIN FETCH o.registrant " +
            "WHERE o.registrant.email = :email " +
            "ORDER BY o.orderTime DESC",
            countQuery = "SELECT COUNT(o) FROM Order o WHERE o.registrant.email = :email")
    Page<Order> findByMemberEmail(@Param("email") String email, Pageable pageable);


    @Query("SELECT o " +
            "FROM Order o " +
            "LEFT JOIN FETCH o.orderItems oi " +
            "LEFT JOIN FETCH oi.product p " +
            "LEFT JOIN FETCH p.gallery " +
            "LEFT JOIN FETCH o.branch " +
            "LEFT JOIN FETCH o.registrant " +
            "WHERE o.orderNo = :orderNo")
    Optional<Order> findByIdWithItems(@Param("orderNo") Long orderNo);


    @Query("SELECT new com.example.cafeis.DTO.OrderListDTO(" +
            "o.orderNo, " +
            "o.registrant.email, " +
            "o.orderTime, " +
            "o.orderStatus, " +
            "o.finalAmount, " +
            "o.cashPaid, " +
            "o.requestedPickupTime, " +
            "o.confirmedPickupTime, " +
            "CAST((SELECT COUNT(oi) FROM OrderItem oi WHERE oi.order = o) AS long)" +
            ") " +
            "FROM Order o " +
            "WHERE o.registrant.email = :email " +
            "ORDER BY o.orderTime DESC")
    Page<OrderListDTO> findByMemberEmailWithItemCount(@Param("email") String email, Pageable pageable);

    @Query("SELECT new com.example.cafeis.DTO.CongestionStatsDTO(" +
            "o.confirmedPickupTime, " +
            "SUM(oi.quantity)) " +
            "FROM Order o " +
            "JOIN o.orderItems oi " +
            "WHERE o.branch.branchNo = :branchNo " +
            "AND o.confirmedPickupTime IS NOT NULL " +
            "AND DATE(o.confirmedPickupTime) = CURRENT_DATE " +
            "AND o.orderStatus IN :statuses " +
            "GROUP BY o.confirmedPickupTime " +
            "ORDER BY o.confirmedPickupTime")
    List<CongestionStatsDTO> getCongestionStatsByBranch(@Param("branchNo") Long branchNo,
                                                        @Param("statuses") List<OrderStatus> statuses);

    @Query("SELECT COUNT(o) " +
            "FROM Order o " +
            "WHERE o.branch.branchNo = :branchNo " +
            "AND o.confirmedPickupTime >= :startTime " +
            "AND o.confirmedPickupTime < :endTime " +
            "AND o.orderStatus IN :statuses")
    Long getOrderCountByTimeRange(@Param("branchNo") Long branchNo,
                                  @Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime,
                                  @Param("statuses") List<OrderStatus> statuses);

    @Query("SELECT COALESCE(SUM(oi.quantity), 0) " +
            "FROM Order o " +
            "JOIN o.orderItems oi " +
            "WHERE o.branch.branchNo = :branchNo " +
            "AND o.confirmedPickupTime >= :startTime " +
            "AND o.confirmedPickupTime < :endTime " +
            "AND o.orderStatus IN :statuses")
    Long getTotalItemQuantityByTimeRange(@Param("branchNo") Long branchNo,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime,
                                         @Param("statuses") List<OrderStatus> statuses);

    @Query("SELECT COUNT(o) " +
            "FROM Order o " +
            "WHERE o.branch.branchNo = :branchNo " +
            "AND o.confirmedPickupTime IS NOT NULL " +
            "AND DATE(o.confirmedPickupTime) = CURRENT_DATE " +
            "AND o.orderStatus IN :statuses")
    Long getTodayOrderCountByBranch(@Param("branchNo") Long branchNo,
                                    @Param("statuses") List<OrderStatus> statuses);

    @Query("SELECT DISTINCT o " +
            "FROM Order o " +
            "LEFT JOIN FETCH o.orderItems oi " +
            "LEFT JOIN FETCH oi.product " +
            "LEFT JOIN FETCH o.registrant " +
            "LEFT JOIN FETCH o.branch " +
            "WHERE o.confirmedPickupTime BETWEEN :startTime AND :endTime " +
            "AND o.orderStatus = :status " +
            "ORDER BY o.confirmedPickupTime ASC")
    List<Order> findOrdersByPickupTimeRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("status") OrderStatus status
    );



    @Query("SELECT COALESCE(SUM(oi.quantity), 0) " +
            "FROM Order o " +
            "JOIN o.orderItems oi " +
            "WHERE o.branch.branchNo = :branchNo " +
            "AND o.confirmedPickupTime IS NOT NULL " +
            "AND DATE(o.confirmedPickupTime) = CURRENT_DATE " +
            "AND o.orderStatus IN :statuses")
    Long getTodayTotalQuantityByBranch(@Param("branchNo") Long branchNo,
                                       @Param("statuses") List<OrderStatus> statuses);

    /**
     *  모든 주문 조회
     */
    @Query("SELECT new com.example.cafeis.DTO.CongestionStatsDTO(" +
            "o.confirmedPickupTime, " +
            "SUM(oi.quantity)) " +
            "FROM Order o " +
            "JOIN o.orderItems oi " +
            "WHERE o.branch.branchNo = :branchNo " +
            "AND o.confirmedPickupTime IS NOT NULL " +
            "GROUP BY o.confirmedPickupTime " +
            "ORDER BY o.confirmedPickupTime")
    List<CongestionStatsDTO> getAllOrdersForDebug(@Param("branchNo") Long branchNo);


}