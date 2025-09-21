package com.cafe.mobile.shcafe.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateRequest {

    @NotNull(message = "주문 ID는 필수입니다.")
    private Long orderId; // 주문ID

    @NotBlank(message = "결제 수단 지정은 필수입니다.")
    private String payMtdCd; // 결제수단코드

    @NotNull(message = "결제 금액은 필수입니다.")
    @Positive(message = "결제 금액은 0보다 커야 합니다.")
    private Integer payAmount; // 결제금액

    @NotNull(message = "멱등성 키는 필수입니다.")
    private String idempotencyKey; // 멱등성 키
}
