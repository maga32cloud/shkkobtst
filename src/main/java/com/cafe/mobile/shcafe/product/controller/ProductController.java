package com.cafe.mobile.shcafe.product.controller;

import com.cafe.mobile.shcafe.common.model.AppResponse;
import com.cafe.mobile.shcafe.common.type.ResponseType;
import com.cafe.mobile.shcafe.product.dto.response.AllProductResponse;
import com.cafe.mobile.shcafe.product.dto.response.CategoryResponse;
import com.cafe.mobile.shcafe.product.dto.response.ProductResponse;
import com.cafe.mobile.shcafe.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1/product")
@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 전체메뉴 조회
    @GetMapping
    public ResponseEntity<AppResponse<List<AllProductResponse>>> getAllProduct() {
        List<AllProductResponse> allProduct = productService.getAllProduct();
        return ResponseEntity.ok(AppResponse.<List<AllProductResponse>>builder()
                .code(ResponseType.SUCCESS.code()).message(ResponseType.SUCCESS.message()).data(allProduct)
                .build());
    }

    // 카테고리 조회
    @GetMapping("/category")
    public ResponseEntity<AppResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> allCategories = productService.getAllCategories();
        return ResponseEntity.ok(AppResponse.<List<CategoryResponse>>builder()
                .code(ResponseType.SUCCESS.code()).message(ResponseType.SUCCESS.message()).data(allCategories)
                .build());
    }

    // 카테고리별 상품 조회
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<AppResponse<List<ProductResponse>>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductResponse> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(AppResponse.<List<ProductResponse>>builder()
                .code(ResponseType.SUCCESS.code()).message(ResponseType.SUCCESS.message()).data(products)
                .build());
    }

    /*
    TODO:
     (관리자)상품생성 : product - insert, product_history - insert
     (관리자)상품업데이트 : product - insert, product_history - insert 및 사용중이던 로우 cls_dtm 업데이트
     */
}
