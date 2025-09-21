package com.cafe.mobile.shcafe.payment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "결제 생성 응답")
public class PaymentCreateResponse {
    @Schema(description = "결제 ID", example = "1")
    private Long paymentId;
    
    @Schema(description = "주문 ID", example = "1")
    private Long orderId;
    
    @Schema(description = "결제 수단 코드", example = "00")
    private String payMtdCd;
    
    @Schema(description = "결제 금액 (원)", example = "10000")
    private Integer payAmount;
    
    @Schema(description = "결제 상태 코드", example = "00")
    private String payStsCd;
}
