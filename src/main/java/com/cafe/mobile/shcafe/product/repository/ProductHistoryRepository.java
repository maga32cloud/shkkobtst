package com.cafe.mobile.shcafe.product.repository;

import com.cafe.mobile.shcafe.product.entity.ProductHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ProductHistoryRepository extends JpaRepository<ProductHistory, Long> {

    // 특정시각 사용중인 상품정보 조회
    @Query("select ph " +
            "from ProductHistory ph " +
            "where ph.productId = :productId " +
            "and ph.regDtm <= :time " +
            "and (:time <= ph.clsDtm or ph.clsDtm is null) " +
            "and ph.useYn = :useYn"
    )
    Optional<ProductHistory> findProductInfoAtTimeUseYn(@Param("productId") Long productId, @Param("time") LocalDateTime time, @Param("useYn") String useYn);

}
