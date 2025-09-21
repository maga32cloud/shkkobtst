package com.cafe.mobile.shcafe.product.service;

import com.cafe.mobile.shcafe.product.dto.response.AllProductResponse;
import com.cafe.mobile.shcafe.product.dto.response.CategoryResponse;
import com.cafe.mobile.shcafe.product.dto.response.ProductResponse;
import com.cafe.mobile.shcafe.product.entity.Category;
import com.cafe.mobile.shcafe.product.entity.Product;
import com.cafe.mobile.shcafe.product.entity.ProductHistory;
import com.cafe.mobile.shcafe.product.repository.CategoryRepository;
import com.cafe.mobile.shcafe.product.repository.ProductHistoryRepository;
import com.cafe.mobile.shcafe.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductHistoryRepository productHistoryRepository;

    public ProductServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository, ProductHistoryRepository productHistoryRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.productHistoryRepository = productHistoryRepository;
    }

    /**
     * 전체 상품 조회
     * @return List<AllProductResponse>
     */
    @Override
    public List<AllProductResponse> getAllProduct() {
        List<AllProductResponse> allProduct = new ArrayList<>();

        // 사용중인 카테고리 조회
        List<Category> categories = categoryRepository.findByUseYnOrderByDisplayOrderAsc("Y");

        // 카테고리별 사용중인 상품 추가
        for (Category category : categories) {
            List<Product> products = productRepository.findUseYnProducts(category.getCategoryId(), "Y");

            List<ProductResponse> productResponses = new ArrayList<>();
            for (Product product : products) {
                productResponses.add(ProductResponse.from(product));
            }

            allProduct.add(AllProductResponse.from(category, productResponses));
        }

        return allProduct;
    }

    /**
     * 전체 카테고리 조회
     * @return List<CategoryResponse>
     */
    @Override
    public List<CategoryResponse> getAllCategories() {
        List<CategoryResponse> allCategories = new ArrayList<>();

        // 사용중인 카테고리 조회
        List<Category> categories = categoryRepository.findByUseYnOrderByDisplayOrderAsc("Y");

        for (Category category : categories) {
            allCategories.add(CategoryResponse.from(category));
        }

        return allCategories;
    };

    /**
     * 카테고리별 상품 조회
     * @param categoryId
     * @return List<ProductResponse>
     */
    @Override
    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        List<ProductResponse> categoryProducts = new ArrayList<>();

        // 사용 중인 상품 조회
        List<Product> products = productRepository.findUseYnProducts(categoryId, "Y");

        for(Product product : products) {
            categoryProducts.add(ProductResponse.from(product));
        }

        return categoryProducts;
    }

    /**
     * 상품 ID로 상품 조회
     * @param productId
     * @param useYn
     * @return Optional<Product>
     */
    @Override
    public Optional<Product> findByProductIdAndUseYn(Long productId, String useYn) {
        return productRepository.findByProductIdAndUseYn(productId, useYn);
    }

    /**
     * 특정 시점 상품 이력 조회
     * @param productId
     * @param time
     * @param useYn
     * @return Optional<ProductHistory>
     */
    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Optional<ProductHistory> findProductInfoAtTimeUseYn(Long productId, LocalDateTime time, String useYn) {
        return productHistoryRepository.findProductInfoAtTimeUseYn(productId, time, useYn);
    }
}
