package com.cafe.mobile.shcafe.payment.service;

import com.cafe.mobile.shcafe.payment.dto.request.PaymentCreateRequest;
import com.cafe.mobile.shcafe.payment.dto.response.PaymentCreateResponse;
import com.cafe.mobile.shcafe.payment.entity.Payment;

import java.util.Optional;

public interface PaymentService {

    PaymentCreateResponse createPayment(PaymentCreateRequest request);

    String mockPaymentTest(Payment payment);

    Optional<Payment> findTopByOrderOrderIdOrderByPaymentIdDesc(Long paymentId);

    Payment save(Payment ayment);
}
