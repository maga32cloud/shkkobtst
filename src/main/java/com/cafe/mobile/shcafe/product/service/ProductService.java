package com.cafe.mobile.shcafe.product.service;

import com.cafe.mobile.shcafe.product.dto.response.AllProductResponse;
import com.cafe.mobile.shcafe.product.dto.response.CategoryResponse;
import com.cafe.mobile.shcafe.product.dto.response.ProductResponse;
import com.cafe.mobile.shcafe.product.entity.Product;
import com.cafe.mobile.shcafe.product.entity.ProductHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 상품 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface ProductService {

    /**
     * 전체 상품 조회
     * @return List<AllProductResponse>
     */
    List<AllProductResponse> getAllProduct();

    /**
     * 전체 카테고리 조회
     * @return List<CategoryResponse>
     */
    List<CategoryResponse> getAllCategories();

    /**
     * 카테고리별 상품 조회
     * @param categoryId
     * @return List<ProductResponse>
     */
    List<ProductResponse> getProductsByCategory(Long categoryId);

    /**
     * 상품 ID로 상품 조회
     * @param productId
     * @param useYn
     * @return Optional<Product>
     */
    Optional<Product> findByProductIdAndUseYn(Long productId, String useYn);

    /**
     * 특정 시점 상품 이력 조회
     * @param productId
     * @param time
     * @param useYn
     * @return Optional<ProductHistory>
     */
    Optional<ProductHistory> findProductInfoAtTimeUseYn(Long productId, LocalDateTime time, String useYn);
}
