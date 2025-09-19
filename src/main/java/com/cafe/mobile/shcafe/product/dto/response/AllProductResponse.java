package com.cafe.mobile.shcafe.product.dto.response;

import com.cafe.mobile.shcafe.product.entity.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AllProductResponse {
    
    private Long categoryId;
    private String categoryName;
    private Integer displayOrder;
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
