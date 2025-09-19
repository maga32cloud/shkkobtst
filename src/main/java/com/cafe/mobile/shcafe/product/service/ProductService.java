package com.cafe.mobile.shcafe.product.service;

import com.cafe.mobile.shcafe.product.dto.response.AllProductResponse;
import com.cafe.mobile.shcafe.product.dto.response.CategoryResponse;
import com.cafe.mobile.shcafe.product.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    List<AllProductResponse> getAllProduct();

    List<CategoryResponse> getAllCategories();

    List<ProductResponse> getProductsByCategory(Long categoryId);
}
