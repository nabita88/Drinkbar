package com.example.cafeis.repository;

import com.example.cafeis.DTO.BasketGoodsListDTO;
import com.example.cafeis.Domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BasketGoodsRepository extends JpaRepository<BasketGoods, Long> {

    @Query("SELECT " +
            "new com.example.cafeis.DTO.BasketGoodsListDTO(" +
            "    bg.basketGoodsNo, " +
            "    bg.quantity, " +
            "    p.productNo, " +
            "    p.productName, " +
            "    p.sellingPrice, " +
            "    pi.imageFileFullName, " +
            "    bg.size, " +
            "    bg.temperature, " +
            "    bg.additionalOptions, " +
            "    bg.additionalPrice" +
            ") " +
            "FROM BasketGoods bg " +
            "INNER JOIN bg.basket b " +
            "LEFT JOIN bg.product p " +
            "LEFT JOIN p.gallery pi " +
            "WHERE b.registrant.email = :email " +
            "AND (INDEX(pi) = 0 OR pi IS NULL) " +
            "ORDER BY bg.basketGoodsNo DESC")
    List<BasketGoodsListDTO> getItemsOfCartDTOByEmail(@Param("email") String email);


    @Query("SELECT bg " +
            "FROM BasketGoods bg " +
            "INNER JOIN bg.basket b " +
            "WHERE b.registrant.email = :email " +
            "AND bg.product.productNo = :productNo")
    BasketGoods getItemOfPno(@Param("email") String email, @Param("productNo") Long productNo);

    @Query("SELECT b.basketNo " +
            "FROM BasketGoods bg " +
            "INNER JOIN bg.basket b " +
            "WHERE bg.basketGoodsNo = :basketGoodsNo")
    Long getCartFromItem(@Param("basketGoodsNo") Long basketGoodsNo);


    @Query("SELECT " +
            "new com.example.cafeis.DTO.BasketGoodsListDTO(" +
            "    bg.basketGoodsNo, " +
            "    bg.quantity, " +
            "    p.productNo, " +
            "    p.productName, " +
            "    p.sellingPrice, " +
            "    pi.imageFileFullName, " +
            "    bg.size, " +
            "    bg.temperature, " +
            "    bg.additionalOptions, " +
            "    bg.additionalPrice" +
            ") " +
            "FROM BasketGoods bg " +
            "INNER JOIN bg.basket b " +
            "LEFT JOIN bg.product p " +
            "LEFT JOIN p.gallery pi " +
            "WHERE b.basketNo = :basketNo " +
            "AND (INDEX(pi) = 0 OR pi IS NULL) " +
            "ORDER BY bg.basketGoodsNo DESC")
    List<BasketGoodsListDTO> getItemsOfCartDTOByCart(@Param("basketNo") Long basketNo);


    @Query("SELECT bg FROM BasketGoods bg WHERE bg.basket = :basket")
    List<BasketGoods> findByBasket(@Param("basket") Basket basket);


    @Query("SELECT " +
            "new com.example.cafeis.DTO.BasketGoodsListDTO(" +
            "    bg.basketGoodsNo, " +
            "    bg.quantity, " +
            "    p.productNo, " +
            "    p.productName, " +
            "    p.sellingPrice, " +
            "    pi.imageFileFullName, " +
            "    bg.size, " +
            "    bg.temperature, " +
            "    bg.additionalOptions, " +
            "    bg.additionalPrice" +
            ") " +
            "FROM BasketGoods bg " +
            "INNER JOIN bg.basket b " +
            "LEFT JOIN bg.product p " +
            "LEFT JOIN p.gallery pi " +
            "WHERE b.registrant.email = :email " +
            "AND b.branch.branchNo = :branchNo " +
            "AND (INDEX(pi) = 0 OR pi IS NULL) " +
            "ORDER BY bg.basketGoodsNo DESC")
    List<BasketGoodsListDTO> getItemsOfCartDTOByEmailAndBranch(@Param("email") String email,
                                                               @Param("branchNo") Long branchNo);
}

