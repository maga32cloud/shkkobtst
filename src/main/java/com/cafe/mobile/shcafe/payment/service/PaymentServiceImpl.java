package com.cafe.mobile.shcafe.payment.service;

import com.cafe.mobile.shcafe.common.exception.BizException;
import com.cafe.mobile.shcafe.common.jwt.JwtUtil;
import com.cafe.mobile.shcafe.common.type.OrderStsCdConst;
import com.cafe.mobile.shcafe.common.type.PayStsCdConst;
import com.cafe.mobile.shcafe.common.type.ResponseType;
import com.cafe.mobile.shcafe.member.entity.Member;
import com.cafe.mobile.shcafe.member.service.MemberService;
import com.cafe.mobile.shcafe.order.entity.Orders;
import com.cafe.mobile.shcafe.order.service.OrderService;
import com.cafe.mobile.shcafe.payment.dto.request.PaymentCreateRequest;
import com.cafe.mobile.shcafe.payment.dto.response.PaymentCreateResponse;
import com.cafe.mobile.shcafe.payment.entity.Payment;
import com.cafe.mobile.shcafe.payment.repository.PaymentRepository;
import com.cafe.mobile.shcafe.payment.util.MockPaymentUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberService memberService;
    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    public PaymentServiceImpl(PaymentRepository paymentRepository, MemberService memberService, OrderService orderService, JwtUtil jwtUtil) {
        this.paymentRepository = paymentRepository;
        this.memberService = memberService;
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public PaymentCreateResponse createPayment(PaymentCreateRequest request) {
        // Jwt ID로 회원 존재 여부 확인
        Member member = memberService.validateActiveMemberByMemberId(jwtUtil.getCurrentMemberId());

        // 주문 존재 여부 확인
        Orders order = orderService.findById(request.getOrderId())
                .orElseThrow(() -> new BizException(ResponseType.INVALID_ORDER));

        // 결제 금액과 주문 금액 일치 확인
        if (!order.getTotalAmount().equals(request.getPayAmount())) {
            throw new BizException(ResponseType.PAY_PRICE_MISMATCH);
        }

        // 멱등성 키 일치시 진행중인 건으로 리턴
        if (paymentRepository.existsByIdempotencyKey(request.getIdempotencyKey())) {
            throw new BizException(ResponseType.PAY_IN_PROGRESS);
        }

        // 주문에 결제정보가 이미 있는경우엔 결제가능여부 확인
        Optional<Payment> existPayment = paymentRepository.findTopByOrderOrderIdOrderByPaymentIdDesc(request.getOrderId());

        if(existPayment.isPresent()) {
            // 결제실패 상태인 경우만 결제시도 가능
            switch (existPayment.get().getPayStsCd()) {
                case PayStsCdConst.PENDING -> throw new BizException(ResponseType.PAY_PENDING); // 결제 진행중
                case PayStsCdConst.SUCCESS -> throw new BizException(ResponseType.ALREADY_PAID); // 이미 지불된 건
                case PayStsCdConst.CANCELED -> throw new BizException(ResponseType.PAY_CANCELED); // 결제 취소 건
            }
        }

        Payment payment = Payment.builder()
                                .order(order)
                                .member(member)
                                .idempotencyKey(request.getIdempotencyKey())
                                .payMtdCd(request.getPayMtdCd())
                                .payAmount(request.getPayAmount())
                                .payStsCd(PayStsCdConst.PENDING)
                                .build();

        Payment savedPayment = null;

        // 대기상태 저장
        try {
            savedPayment = paymentRepository.save(payment);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new BizException(ResponseType.PAY_IN_PROGRESS);
            }
            throw e;
        }

        // (테스트)결제 진행
        String payResult = mockPaymentTest(savedPayment);

        Payment finalPayment = savedPayment.copy();

        // 결제 오류시
        if(payResult.equals("결제오류")) {
            finalPayment.setPayStsCd(PayStsCdConst.FAILED);
        } else {
            // 결제 성공시
            finalPayment.setPayStsCd(PayStsCdConst.SUCCESS);
            finalPayment.setTransactionId(payResult);
            finalPayment.setPaidDtm(LocalDateTime.now());
        }

        // 최종상태 저장
        paymentRepository.save(finalPayment);

        // 성공시 주문상태 변경요청
        if (PayStsCdConst.SUCCESS.equals(finalPayment.getPayStsCd())) {
            orderService.transOrderStatus(order.getOrderId(), OrderStsCdConst.ORDER_RECEIVED);
        }

        return PaymentCreateResponse.builder()
                .paymentId(finalPayment.getPaymentId())
                .orderId(finalPayment.getOrder().getOrderId())
                .payAmount(finalPayment.getPayAmount())
                .payMtdCd(finalPayment.getPayMtdCd())
                .payStsCd(finalPayment.getPayStsCd())
                .build();

    }

    // 결제 mock(Transaction Exeption 전파 분리)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String mockPaymentTest(Payment payment) {
        try {
            return MockPaymentUtil.makePayment();
        } catch(Exception e) {
            return "결제오류";
        }
    }
}
