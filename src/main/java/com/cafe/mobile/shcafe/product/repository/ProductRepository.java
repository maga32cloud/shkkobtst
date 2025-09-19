package com.cafe.mobile.shcafe.product.repository;

import com.cafe.mobile.shcafe.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select p " +
            "from Product p " +
            "where p.category.categoryId = :categoryId " +
            "and p.useYn = :useYn " +
            "order by p.displayOrder"
    )
    List<Product> findUseYnProducts(@Param("categoryId") Long categoryId, @Param("useYn") String useYn);
}
