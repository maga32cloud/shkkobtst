package com.cafe.mobile.shcafe.product.dto.response;

import com.cafe.mobile.shcafe.product.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "전체 상품 조회 응답 (카테고리별)")
public class AllProductResponse {
    
    @Schema(description = "카테고리 ID", example = "1")
    private Long categoryId;
    
    @Schema(description = "카테고리 이름", example = "커피")
    private String categoryName;
    
    @Schema(description = "표시 순서", example = "1")
    private Integer displayOrder;
    
    @Schema(description = "상품 목록")
    private List<ProductResponse> products;
    
    public static AllProductResponse from(Category category, List<ProductResponse> products) {
        return AllProductResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .displayOrder(category.getDisplayOrder())
                .products(products)
                .build();
    }
}
