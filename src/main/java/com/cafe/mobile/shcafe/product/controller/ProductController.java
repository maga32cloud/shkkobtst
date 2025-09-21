package com.cafe.mobile.shcafe.product.controller;

import com.cafe.mobile.shcafe.common.model.AppResponse;
import com.cafe.mobile.shcafe.common.type.ResponseType;
import com.cafe.mobile.shcafe.product.dto.response.AllProductResponse;
import com.cafe.mobile.shcafe.product.dto.response.CategoryResponse;
import com.cafe.mobile.shcafe.product.dto.response.ProductResponse;
import com.cafe.mobile.shcafe.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1/product")
@RestController
@Tag(name = "상품 관리", description = "상품 조회 관련 API")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "전체 메뉴 조회", description = "모든 상품을 카테고리별로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<AppResponse<List<AllProductResponse>>> getAllProduct() {
        List<AllProductResponse> allProduct = productService.getAllProduct();

        return ResponseEntity.ok(AppResponse.<List<AllProductResponse>>builder()
                .code(ResponseType.SUCCESS.code()).message(ResponseType.SUCCESS.message()).data(allProduct)
                .build());
    }

    @Operation(summary = "카테고리 조회", description = "모든 상품 카테고리를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/category")
    public ResponseEntity<AppResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> allCategories = productService.getAllCategories();

        return ResponseEntity.ok(AppResponse.<List<CategoryResponse>>builder()
                .code(ResponseType.SUCCESS.code()).message(ResponseType.SUCCESS.message()).data(allCategories)
                .build());
    }

    @Operation(summary = "카테고리별 상품 조회", description = "특정 카테고리의 상품들을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<AppResponse<List<ProductResponse>>> getProductsByCategory(
            @Parameter(description = "카테고리 ID", required = true) @PathVariable Long categoryId) {
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
