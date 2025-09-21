package com.cafe.mobile.shcafe.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "주문 생성 응답")
public class OrderCreateResponse {
    @Schema(description = "생성된 주문 ID", example = "1")
    private Long orderId;
}
