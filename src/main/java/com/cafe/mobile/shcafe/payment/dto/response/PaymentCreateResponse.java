package com.cafe.mobile.shcafe.payment.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentCreateResponse {
    private Long paymentId;
    private Long orderId;
    private String payMtdCd;
    private Integer payAmount;
    private String payStsCd;
}
