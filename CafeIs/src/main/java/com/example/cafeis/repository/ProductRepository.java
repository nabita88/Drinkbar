package com.example.cafeis.repository;

import com.example.cafeis.Domain.Product;
import com.example.cafeis.Enum.ProductGroupType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>{



    @EntityGraph(attributePaths = {"gallery", "branch"})
    @Query("""
           SELECT p
             FROM Product p
            WHERE p.productNo = :productNo
           """)
    Optional<Product> selectOne(@Param("productNo") Long productNo);



    @Query("""
           SELECT p, pi
             FROM Product p
        LEFT JOIN FETCH p.branch
        LEFT JOIN p.gallery pi
               ON INDEX(pi) = 0
            WHERE p.isDeleted = false
              AND p.category = :category
         ORDER BY p.productNo DESC
           """)
    List<Object[]> findByCategory(@Param("category") ProductGroupType category);

    @Query("""
           SELECT p, pi
             FROM Product p
        LEFT JOIN FETCH p.branch
        LEFT JOIN p.gallery pi
               ON INDEX(pi) = 0
            WHERE p.isDeleted = false
              AND p.category = :category
              AND p.branch.branchNo = :branchNo
         ORDER BY p.productNo DESC
           """)
    List<Object[]> findByCategoryAndBranch(@Param("category") ProductGroupType category,
                                           @Param("branchNo") Long branchNo);

    @Query("""
           SELECT p, pi
             FROM Product p
        LEFT JOIN FETCH p.branch
        LEFT JOIN p.gallery pi
               ON INDEX(pi) = 0
            WHERE p.isDeleted = false
           """)
    Page<Object[]> findAllWithImages(Pageable pageable);
}