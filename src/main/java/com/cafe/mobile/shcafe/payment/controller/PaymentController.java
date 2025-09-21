package com.cafe.mobile.shcafe.payment.controller;

import com.cafe.mobile.shcafe.common.model.AppResponse;
import com.cafe.mobile.shcafe.common.type.ResponseType;
import com.cafe.mobile.shcafe.payment.dto.request.PaymentCreateRequest;
import com.cafe.mobile.shcafe.payment.dto.response.PaymentCreateResponse;
import com.cafe.mobile.shcafe.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/payment")
@RestController
@Tag(name = "결제 관리", description = "결제 처리 관련 API")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "결제 처리", description = "주문에 대한 결제를 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "결제 처리 실패")
    })
    @PostMapping
    public ResponseEntity<AppResponse<PaymentCreateResponse>> createPayment(@RequestBody @Valid PaymentCreateRequest request) {
        PaymentCreateResponse payment = paymentService.createPayment(request);

        return ResponseEntity.ok(AppResponse.<PaymentCreateResponse>builder()
                .code(ResponseType.SUCCESS.code()).message(ResponseType.SUCCESS.message())
                .data(payment)
                .build());
    }

}
