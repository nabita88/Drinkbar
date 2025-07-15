package com.example.cafeis.repository;


import com.example.cafeis.Domain.Basket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BasketRepository extends JpaRepository<Basket, Long> {

    @Query("SELECT b " +
            "FROM Basket b " +
            "WHERE b.registrant.email = :email")
    Optional<Basket> getCartOfMember(@Param("email") String email);

    @Query("SELECT b FROM Basket b WHERE b.registrant.email = :email")
    Optional<Basket> findByRegistrantEmail(@Param("email") String email);

    @Query("SELECT DISTINCT b FROM Basket b " +
            "LEFT JOIN FETCH b.basketGoodsPerson bg " +
            "LEFT JOIN FETCH bg.product p " +
            "LEFT JOIN FETCH p.gallery " +
            "LEFT JOIN FETCH b.branch " +
            "LEFT JOIN FETCH b.registrant " +
            "WHERE b.registrant.email = :email")
    Optional<Basket> findByMemberEmailWithItems(@Param("email") String email);

    @Query("SELECT b FROM Basket b " +
            "WHERE b.registrant.email = :email " +
            "AND b.branch.branchNo = :branchNo")
    Optional<Basket> findByRegistrantEmailAndBranchNo(@Param("email") String email,
                                                      @Param("branchNo") Long branchNo);

    @Query("SELECT DISTINCT b FROM Basket b " +
            "LEFT JOIN FETCH b.basketGoodsPerson bg " +
            "LEFT JOIN FETCH bg.product p " +
            "LEFT JOIN FETCH p.gallery " +
            "LEFT JOIN FETCH b.branch " +
            "LEFT JOIN FETCH b.registrant " +
            "WHERE b.registrant.email = :email")
    Optional<Basket> findByRegistrantWithItems(@Param("email") String email);
}

