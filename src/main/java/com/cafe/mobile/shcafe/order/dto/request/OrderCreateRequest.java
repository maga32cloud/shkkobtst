package com.cafe.mobile.shcafe.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 생성 요청")
public class OrderCreateRequest {

    @NotEmpty(message = "주문 상품은 최소 1개 이상이어야 합니다.")
    @Valid
    @Schema(description = "주문 상품 목록", example = "[{\"productId\": 1, \"quantity\": 2, \"price\": 5000}]", required = true)
    private List<OrderItemRequest> orderItems;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "주문 상품 정보")
    public static class OrderItemRequest {
        
        @NotNull(message = "상품 ID는 필수입니다.")
        @Schema(description = "상품 ID", example = "1", required = true)
        private Long productId;
        
        @NotNull(message = "수량은 필수입니다.")
        @Schema(description = "주문 수량", example = "2", required = true)
        private Integer quantity;
        
        @NotNull(message = "가격은 필수입니다.")
        @Positive(message = "가격은 0보다 커야 합니다.")
        @Schema(description = "상품 가격 (원)", example = "5000", required = true)
        private Integer price;
    }
}
