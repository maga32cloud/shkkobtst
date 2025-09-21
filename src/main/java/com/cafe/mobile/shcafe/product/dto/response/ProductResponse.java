package com.cafe.mobile.shcafe.product.dto.response;

import com.cafe.mobile.shcafe.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "상품 정보")
public class ProductResponse {
    
    @Schema(description = "상품 ID", example = "1")
    private Long productId;
    
    @Schema(description = "상품명", example = "아메리카노")
    private String productName;
    
    @Schema(description = "상품 설명", example = "깔끔하고 진한 맛의 아메리카노")
    private String description;
    
    @Schema(description = "상품 가격 (원)", example = "4500")
    private Integer price;
    
    @Schema(description = "상품 이미지 URL", example = "https://example.com/images/americano.jpg")
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
