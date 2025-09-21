package com.cafe.mobile.shcafe.payment.listener;

import com.cafe.mobile.shcafe.common.exception.BizException;
import com.cafe.mobile.shcafe.common.type.PayStsCdConst;
import com.cafe.mobile.shcafe.common.type.ResponseType;
import com.cafe.mobile.shcafe.order.event.OrderCancelledEvent;
import com.cafe.mobile.shcafe.payment.entity.Payment;
import com.cafe.mobile.shcafe.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class PaymentCancellationListener {
    
    private final PaymentService paymentService;

    public PaymentCancellationListener(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @EventListener
    public void handleOrderCancelled(OrderCancelledEvent event) {
        Long orderId = event.getOrderId();
        String memberId = event.getMemberId();

        // 최신 결제내역 가져오기
        Payment payment = paymentService.findTopByOrderOrderIdOrderByPaymentIdDesc(orderId)
                .orElseThrow(() -> new BizException(ResponseType.NO_PAYMENT));

        if(PayStsCdConst.CANCELED.equals(payment.getPayStsCd())) {
            throw new BizException(ResponseType.PAY_CANCELED);
        }

        // (테스트)결제취소 진행
        String payResult = "";

        try {
            payResult = paymentService.mockPaymentTest(payment);
        } catch(Exception e) {
            log.debug("결제중 오류발생");
            throw new BizException(ResponseType.CANCEL_PAY_ERRER);
            // TODO: 반복 재수행 로직 필요
        }

        // 결제내역 수정
        Payment canceledPayment = payment.copy();
        canceledPayment.setTransactionId(payResult);
        canceledPayment.setPayStsCd(PayStsCdConst.CANCELED);
        canceledPayment.setCancelDtm(LocalDateTime.now());

        // 결제취소내역 저장
        paymentService.save(canceledPayment);
    }
}
