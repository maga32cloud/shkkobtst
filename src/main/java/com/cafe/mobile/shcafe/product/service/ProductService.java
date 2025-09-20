package com.cafe.mobile.shcafe.product.service;

import com.cafe.mobile.shcafe.product.dto.response.AllProductResponse;
import com.cafe.mobile.shcafe.product.dto.response.CategoryResponse;
import com.cafe.mobile.shcafe.product.dto.response.ProductResponse;
import com.cafe.mobile.shcafe.product.entity.Product;
import com.cafe.mobile.shcafe.product.entity.ProductHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<AllProductResponse> getAllProduct();

    List<CategoryResponse> getAllCategories();

    List<ProductResponse> getProductsByCategory(Long categoryId);

    Optional<Product> findByProductIdAndUseYn(Long productId, String y);

    Optional<ProductHistory> findProductInfoAtTimeUseYn(Long productId, LocalDateTime time, String useYn);
}
