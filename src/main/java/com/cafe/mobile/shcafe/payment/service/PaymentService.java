package com.cafe.mobile.shcafe.payment.service;

import com.cafe.mobile.shcafe.payment.dto.request.PaymentCreateRequest;
import com.cafe.mobile.shcafe.payment.dto.response.PaymentCreateResponse;

public interface PaymentService {

    PaymentCreateResponse createPayment(PaymentCreateRequest request);

}
