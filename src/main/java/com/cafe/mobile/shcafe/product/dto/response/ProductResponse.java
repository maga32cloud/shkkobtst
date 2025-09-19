package com.cafe.mobile.shcafe.product.dto.response;

import com.cafe.mobile.shcafe.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponse {
    
    private Long productId;
    private String productName;
    private String description;
    private Integer price;
    private String imgUrl;
    
    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imgUrl(product.getImgUrl())
                .build();
    }
}
