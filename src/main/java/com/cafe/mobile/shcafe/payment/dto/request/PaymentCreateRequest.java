package com.cafe.mobile.shcafe.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "결제 생성 요청")
public class PaymentCreateRequest {

    @NotNull(message = "주문 ID는 필수입니다.")
    @Schema(description = "주문 ID", example = "1", required = true)
    private Long orderId; // 주문ID

    @NotBlank(message = "결제 수단 지정은 필수입니다.")
    @Schema(description = "결제 수단 코드", example = "00", allowableValues = {"00", "01", "02"}, required = true)
    private String payMtdCd; // 결제수단코드

    @NotNull(message = "결제 금액은 필수입니다.")
    @Positive(message = "결제 금액은 0보다 커야 합니다.")
    @Schema(description = "결제 금액 (원)", example = "10000", required = true)
    private Integer payAmount; // 결제금액

    @NotNull(message = "멱등성 키는 필수입니다.")
    @Schema(description = "멱등성 키 (중복 결제 방지용UUID)", example = "8119a572-4d07-49b4-b8af-43d186df20cc", required = true)
    private String idempotencyKey; // 멱등성 키
}
