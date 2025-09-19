package com.cafe.mobile.shcafe.product.service;

import com.cafe.mobile.shcafe.product.dto.response.AllProductResponse;
import com.cafe.mobile.shcafe.product.dto.response.CategoryResponse;
import com.cafe.mobile.shcafe.product.dto.response.ProductResponse;
import com.cafe.mobile.shcafe.product.entity.Category;
import com.cafe.mobile.shcafe.product.entity.Product;
import com.cafe.mobile.shcafe.product.repository.CategoryRepository;
import com.cafe.mobile.shcafe.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public ProductServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

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
}
