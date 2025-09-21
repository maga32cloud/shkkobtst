package com.cafe.mobile.shcafe.product.dto.response;

import com.cafe.mobile.shcafe.product.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "카테고리 정보")
public class CategoryResponse {
    
    @Schema(description = "카테고리 ID", example = "1")
    private Long categoryId;
    
    @Schema(description = "카테고리 이름", example = "커피")
    private String categoryName;
    
    @Schema(description = "표시 순서", example = "1")
    private Integer displayOrder;
    
    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .displayOrder(category.getDisplayOrder())
                .build();
    }
}
