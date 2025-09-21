package com.cafe.mobile.shcafe.payment.controller;

import com.cafe.mobile.shcafe.common.model.AppResponse;
import com.cafe.mobile.shcafe.common.type.ResponseType;
import com.cafe.mobile.shcafe.payment.dto.request.PaymentCreateRequest;
import com.cafe.mobile.shcafe.payment.dto.response.PaymentCreateResponse;
import com.cafe.mobile.shcafe.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/payment")
@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // 결제
    @PostMapping
    public ResponseEntity<AppResponse<PaymentCreateResponse>> createPayment(@RequestBody @Valid PaymentCreateRequest request) {
        PaymentCreateResponse payment = paymentService.createPayment(request);

        return ResponseEntity.ok(AppResponse.<PaymentCreateResponse>builder()
                .code(ResponseType.SUCCESS.code()).message(ResponseType.SUCCESS.message())
                .data(payment)
                .build());
    }

}
