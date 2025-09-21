package com.cafe.mobile.shcafe.payment.service;

import com.cafe.mobile.shcafe.payment.dto.request.PaymentCreateRequest;
import com.cafe.mobile.shcafe.payment.dto.response.PaymentCreateResponse;
import com.cafe.mobile.shcafe.payment.entity.Payment;

import java.util.Optional;

public interface PaymentService {

    /**
     * 결제 생성
     * @param request
     * @return PaymentCreateResponse
     */
    PaymentCreateResponse createPayment(PaymentCreateRequest request);

    /**
     * Mock 결제 테스트 샘플
     * @param payment
     * @return String
     */
    String mockPaymentTest(Payment payment);

    /**
     * 주문 ID로 최신 결제 조회
     * @param paymentId
     * @return Optional<Payment>
     */
    Optional<Payment> findTopByOrderOrderIdOrderByPaymentIdDesc(Long paymentId);

    /**
     * 결제 저장
     * @param payment
     * @return Payment
     */
    Payment save(Payment payment);
}
