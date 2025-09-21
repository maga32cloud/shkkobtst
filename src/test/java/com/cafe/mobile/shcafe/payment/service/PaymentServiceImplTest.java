package com.cafe.mobile.shcafe.payment.service;

import com.cafe.mobile.shcafe.common.exception.BizException;
import com.cafe.mobile.shcafe.common.jwt.JwtUtil;
import com.cafe.mobile.shcafe.common.type.OrderStsCdConst;
import com.cafe.mobile.shcafe.common.type.PayStsCdConst;
import com.cafe.mobile.shcafe.common.type.ResponseType;
import com.cafe.mobile.shcafe.common.util.MockPaymentUtil;
import com.cafe.mobile.shcafe.member.entity.Member;
import com.cafe.mobile.shcafe.member.service.MemberService;
import com.cafe.mobile.shcafe.order.entity.Orders;
import com.cafe.mobile.shcafe.order.service.OrderService;
import com.cafe.mobile.shcafe.payment.dto.request.PaymentCreateRequest;
import com.cafe.mobile.shcafe.payment.dto.response.PaymentCreateResponse;
import com.cafe.mobile.shcafe.payment.entity.Payment;
import com.cafe.mobile.shcafe.payment.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService 테스트")
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private OrderService orderService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Nested
    @DisplayName("결제 생성 테스트")
    class CreatePaymentTest {

        @Test
        @DisplayName("정상 케이스 - MockPaymentUtil 성공한 경우")
        void success_mockPaymentUtilSuccess() {
            // Given
            String memberId = "testuser123";
            Long orderId = 1L;
            String idempotencyKey = "test-key-123";
            String payMtdCd = "00";
            Integer payAmount = 10000;

            Member member = Member.builder()
                    .memberId(memberId)
                    .memberStsCd("00")
                    .regDt(LocalDateTime.now().toLocalDate())
                    .build();

            Orders order = Orders.builder()
                    .orderId(orderId)
                    .member(member)
                    .totalAmount(payAmount)
                    .orderStsCd(OrderStsCdConst.PAYMENT_PENDING)
                    .regDtm(LocalDateTime.now())
                    .build();

            PaymentCreateRequest request = new PaymentCreateRequest(orderId, payMtdCd, payAmount, idempotencyKey);

            when(jwtUtil.getCurrentMemberId()).thenReturn(memberId);
            when(memberService.validateActiveMemberByMemberId(memberId)).thenReturn(member);
            when(orderService.findById(orderId)).thenReturn(Optional.of(order));
            when(paymentRepository.existsByIdempotencyKey(idempotencyKey)).thenReturn(false);
            when(paymentRepository.findTopByOrderOrderIdOrderByPaymentIdDesc(orderId)).thenReturn(Optional.empty());
            when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
                Payment p = invocation.getArgument(0);
                try {
                    java.lang.reflect.Field field = Payment.class.getDeclaredField("paymentId");
                    field.setAccessible(true);
                    field.set(p, 1L);
                } catch (Exception e) {
                    // Reflection 실패시 기본값 사용
                }
                return p;
            });

            // MockPaymentUtil.makePayment()가 "Success"를 반환하도록 Mock 설정
            try (MockedStatic<MockPaymentUtil> mockedStatic = mockStatic(MockPaymentUtil.class)) {
                mockedStatic.when(MockPaymentUtil::makePayment).thenReturn("Success");

                // When
                PaymentCreateResponse response = paymentService.createPayment(request);

                // Then
                assertThat(response.getPaymentId()).isEqualTo(1L);
                assertThat(response.getOrderId()).isEqualTo(orderId);
                assertThat(response.getPayAmount()).isEqualTo(payAmount);
                assertThat(response.getPayMtdCd()).isEqualTo(payMtdCd);
                assertThat(response.getPayStsCd()).isEqualTo(PayStsCdConst.SUCCESS);

                verify(jwtUtil).getCurrentMemberId();
                verify(memberService).validateActiveMemberByMemberId(memberId);
                verify(orderService).findById(orderId);
                verify(paymentRepository).existsByIdempotencyKey(idempotencyKey);
                verify(paymentRepository).findTopByOrderOrderIdOrderByPaymentIdDesc(orderId);
                verify(paymentRepository, times(2)).save(any(Payment.class)); // 대기상태 저장 + 최종상태 저장
                verify(orderService).transOrderStatus(orderId, OrderStsCdConst.ORDER_RECEIVED); // 성공시 주문상태 변경
            }
        }

        @Test
        @DisplayName("정상 케이스 - MockPaymentUtil 실패한 경우")
        void success_mockPaymentUtilFailure() {
            // Given
            String memberId = "testuser123";
            Long orderId = 1L;
            String idempotencyKey = "test-key-123";
            String payMtdCd = "00";
            Integer payAmount = 10000;

            Member member = Member.builder()
                    .memberId(memberId)
                    .memberStsCd("00")
                    .regDt(LocalDateTime.now().toLocalDate())
                    .build();

            Orders order = Orders.builder()
                    .orderId(orderId)
                    .member(member)
                    .totalAmount(payAmount)
                    .orderStsCd(OrderStsCdConst.PAYMENT_PENDING)
                    .regDtm(LocalDateTime.now())
                    .build();

            PaymentCreateRequest request = new PaymentCreateRequest(orderId, payMtdCd, payAmount, idempotencyKey);

            when(jwtUtil.getCurrentMemberId()).thenReturn(memberId);
            when(memberService.validateActiveMemberByMemberId(memberId)).thenReturn(member);
            when(orderService.findById(orderId)).thenReturn(Optional.of(order));
            when(paymentRepository.existsByIdempotencyKey(idempotencyKey)).thenReturn(false);
            when(paymentRepository.findTopByOrderOrderIdOrderByPaymentIdDesc(orderId)).thenReturn(Optional.empty());
            when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
                Payment p = invocation.getArgument(0);
                try {
                    java.lang.reflect.Field field = Payment.class.getDeclaredField("paymentId");
                    field.setAccessible(true);
                    field.set(p, 1L);
                } catch (Exception e) {
                    // Reflection 실패시 기본값 사용
                }
                return p;
            });

            // MockPaymentUtil.makePayment()가 예외를 발생시키도록 Mock 설정
            try (MockedStatic<MockPaymentUtil> mockedStatic = mockStatic(MockPaymentUtil.class)) {
                mockedStatic.when(MockPaymentUtil::makePayment).thenThrow(new Exception("Failed"));

                // When
                PaymentCreateResponse response = paymentService.createPayment(request);

                // Then
                assertThat(response.getPaymentId()).isEqualTo(1L);
                assertThat(response.getOrderId()).isEqualTo(orderId);
                assertThat(response.getPayAmount()).isEqualTo(payAmount);
                assertThat(response.getPayMtdCd()).isEqualTo(payMtdCd);
                assertThat(response.getPayStsCd()).isEqualTo(PayStsCdConst.FAILED);

                verify(jwtUtil).getCurrentMemberId();
                verify(memberService).validateActiveMemberByMemberId(memberId);
                verify(orderService).findById(orderId);
                verify(paymentRepository).existsByIdempotencyKey(idempotencyKey);
                verify(paymentRepository).findTopByOrderOrderIdOrderByPaymentIdDesc(orderId);
                verify(paymentRepository, times(2)).save(any(Payment.class)); // 대기상태 저장 + 최종상태 저장
                verify(orderService, never()).transOrderStatus(any(), any()); // 실패시 주문상태 변경 안됨
            }
        }

        @Test
        @DisplayName("존재하지 않는 주문")
        void invalidOrder_throwsException() {
            // Given
            String memberId = "testuser123";
            Long orderId = 999L;
            String idempotencyKey = "test-key-123";
            String payMtdCd = "00";
            Integer payAmount = 10000;

            Member member = Member.builder()
                    .memberId(memberId)
                    .memberStsCd("00")
                    .regDt(LocalDateTime.now().toLocalDate())
                    .build();

            PaymentCreateRequest request = new PaymentCreateRequest(orderId, payMtdCd, payAmount, idempotencyKey);

            when(jwtUtil.getCurrentMemberId()).thenReturn(memberId);
            when(memberService.validateActiveMemberByMemberId(memberId)).thenReturn(member);
            when(orderService.findById(orderId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> paymentService.createPayment(request))
                    .isInstanceOf(BizException.class)
                    .hasMessage(ResponseType.INVALID_ORDER.message());

            verify(jwtUtil).getCurrentMemberId();
            verify(memberService).validateActiveMemberByMemberId(memberId);
            verify(orderService).findById(orderId);
            verify(paymentRepository, never()).save(any());
        }

        @Test
        @DisplayName("결제 금액과 주문 금액 불일치")
        void payAmountMismatch_throwsException() {
            // Given
            String memberId = "testuser123";
            Long orderId = 1L;
            String idempotencyKey = "test-key-123";
            String payMtdCd = "00";
            Integer payAmount = 10000;
            Integer orderAmount = 15000; // 다른 금액

            Member member = Member.builder()
                    .memberId(memberId)
                    .memberStsCd("00")
                    .regDt(LocalDateTime.now().toLocalDate())
                    .build();

            Orders order = Orders.builder()
                    .orderId(orderId)
                    .member(member)
                    .totalAmount(orderAmount)
                    .orderStsCd(OrderStsCdConst.PAYMENT_PENDING)
                    .regDtm(LocalDateTime.now())
                    .build();

            PaymentCreateRequest request = new PaymentCreateRequest(orderId, payMtdCd, payAmount, idempotencyKey);

            when(jwtUtil.getCurrentMemberId()).thenReturn(memberId);
            when(memberService.validateActiveMemberByMemberId(memberId)).thenReturn(member);
            when(orderService.findById(orderId)).thenReturn(Optional.of(order));

            // When & Then
            assertThatThrownBy(() -> paymentService.createPayment(request))
                    .isInstanceOf(BizException.class)
                    .hasMessage(ResponseType.PAY_PRICE_MISMATCH.message());

            verify(jwtUtil).getCurrentMemberId();
            verify(memberService).validateActiveMemberByMemberId(memberId);
            verify(orderService).findById(orderId);
            verify(paymentRepository, never()).save(any());
        }

        @Test
        @DisplayName("멱등성 키 중복")
        void duplicateIdempotencyKey_throwsException() {
            // Given
            String memberId = "testuser123";
            Long orderId = 1L;
            String idempotencyKey = "duplicate-key-123";
            String payMtdCd = "00";
            Integer payAmount = 10000;

            Member member = Member.builder()
                    .memberId(memberId)
                    .memberStsCd("00")
                    .regDt(LocalDateTime.now().toLocalDate())
                    .build();

            Orders order = Orders.builder()
                    .orderId(orderId)
                    .member(member)
                    .totalAmount(payAmount)
                    .orderStsCd(OrderStsCdConst.PAYMENT_PENDING)
                    .regDtm(LocalDateTime.now())
                    .build();

            PaymentCreateRequest request = new PaymentCreateRequest(orderId, payMtdCd, payAmount, idempotencyKey);

            when(jwtUtil.getCurrentMemberId()).thenReturn(memberId);
            when(memberService.validateActiveMemberByMemberId(memberId)).thenReturn(member);
            when(orderService.findById(orderId)).thenReturn(Optional.of(order));
            when(paymentRepository.existsByIdempotencyKey(idempotencyKey)).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> paymentService.createPayment(request))
                    .isInstanceOf(BizException.class)
                    .hasMessage(ResponseType.PAY_IN_PROGRESS.message());

            verify(jwtUtil).getCurrentMemberId();
            verify(memberService).validateActiveMemberByMemberId(memberId);
            verify(orderService).findById(orderId);
            verify(paymentRepository).existsByIdempotencyKey(idempotencyKey);
            verify(paymentRepository, never()).save(any());
        }

        @Test
        @DisplayName("이미 결제 진행중인 주문")
        void paymentPending_throwsException() {
            // Given
            String memberId = "testuser123";
            Long orderId = 1L;
            String idempotencyKey = "test-key-123";
            String payMtdCd = "00";
            Integer payAmount = 10000;

            Member member = Member.builder()
                    .memberId(memberId)
                    .memberStsCd("00")
                    .regDt(LocalDateTime.now().toLocalDate())
                    .build();

            Orders order = Orders.builder()
                    .orderId(orderId)
                    .member(member)
                    .totalAmount(payAmount)
                    .orderStsCd(OrderStsCdConst.PAYMENT_PENDING)
                    .regDtm(LocalDateTime.now())
                    .build();

            Payment existingPayment = Payment.builder()
                    .paymentId(1L)
                    .order(order)
                    .member(member)
                    .payStsCd(PayStsCdConst.PENDING) // 진행중
                    .regDtm(LocalDateTime.now())
                    .build();

            PaymentCreateRequest request = new PaymentCreateRequest(orderId, payMtdCd, payAmount, idempotencyKey);

            when(jwtUtil.getCurrentMemberId()).thenReturn(memberId);
            when(memberService.validateActiveMemberByMemberId(memberId)).thenReturn(member);
            when(orderService.findById(orderId)).thenReturn(Optional.of(order));
            when(paymentRepository.existsByIdempotencyKey(idempotencyKey)).thenReturn(false);
            when(paymentRepository.findTopByOrderOrderIdOrderByPaymentIdDesc(orderId)).thenReturn(Optional.of(existingPayment));

            // When & Then
            assertThatThrownBy(() -> paymentService.createPayment(request))
                    .isInstanceOf(BizException.class)
                    .hasMessage(ResponseType.PAY_PENDING.message());

            verify(jwtUtil).getCurrentMemberId();
            verify(memberService).validateActiveMemberByMemberId(memberId);
            verify(orderService).findById(orderId);
            verify(paymentRepository).existsByIdempotencyKey(idempotencyKey);
            verify(paymentRepository).findTopByOrderOrderIdOrderByPaymentIdDesc(orderId);
            verify(paymentRepository, never()).save(any());
        }

        @Test
        @DisplayName("이미 결제 완료된 주문")
        void alreadyPaid_throwsException() {
            // Given
            String memberId = "testuser123";
            Long orderId = 1L;
            String idempotencyKey = "test-key-123";
            String payMtdCd = "00";
            Integer payAmount = 10000;

            Member member = Member.builder()
                    .memberId(memberId)
                    .memberStsCd("00")
                    .regDt(LocalDateTime.now().toLocalDate())
                    .build();

            Orders order = Orders.builder()
                    .orderId(orderId)
                    .member(member)
                    .totalAmount(payAmount)
                    .orderStsCd(OrderStsCdConst.PAYMENT_PENDING)
                    .regDtm(LocalDateTime.now())
                    .build();

            Payment existingPayment = Payment.builder()
                    .paymentId(1L)
                    .order(order)
                    .member(member)
                    .payStsCd(PayStsCdConst.SUCCESS) // 이미 결제 완료
                    .regDtm(LocalDateTime.now())
                    .build();

            PaymentCreateRequest request = new PaymentCreateRequest(orderId, payMtdCd, payAmount, idempotencyKey);

            when(jwtUtil.getCurrentMemberId()).thenReturn(memberId);
            when(memberService.validateActiveMemberByMemberId(memberId)).thenReturn(member);
            when(orderService.findById(orderId)).thenReturn(Optional.of(order));
            when(paymentRepository.existsByIdempotencyKey(idempotencyKey)).thenReturn(false);
            when(paymentRepository.findTopByOrderOrderIdOrderByPaymentIdDesc(orderId)).thenReturn(Optional.of(existingPayment));

            // When & Then
            assertThatThrownBy(() -> paymentService.createPayment(request))
                    .isInstanceOf(BizException.class)
                    .hasMessage(ResponseType.ALREADY_PAID.message());

            verify(jwtUtil).getCurrentMemberId();
            verify(memberService).validateActiveMemberByMemberId(memberId);
            verify(orderService).findById(orderId);
            verify(paymentRepository).existsByIdempotencyKey(idempotencyKey);
            verify(paymentRepository).findTopByOrderOrderIdOrderByPaymentIdDesc(orderId);
            verify(paymentRepository, never()).save(any());
        }

        @Test
        @DisplayName("이미 취소된 결제")
        void paymentCanceled_throwsException() {
            // Given
            String memberId = "testuser123";
            Long orderId = 1L;
            String idempotencyKey = "test-key-123";
            String payMtdCd = "00";
            Integer payAmount = 10000;

            Member member = Member.builder()
                    .memberId(memberId)
                    .memberStsCd("00")
                    .regDt(LocalDateTime.now().toLocalDate())
                    .build();

            Orders order = Orders.builder()
                    .orderId(orderId)
                    .member(member)
                    .totalAmount(payAmount)
                    .orderStsCd(OrderStsCdConst.PAYMENT_PENDING)
                    .regDtm(LocalDateTime.now())
                    .build();

            Payment existingPayment = Payment.builder()
                    .paymentId(1L)
                    .order(order)
                    .member(member)
                    .payStsCd(PayStsCdConst.CANCELED) // 이미 취소됨
                    .regDtm(LocalDateTime.now())
                    .build();

            PaymentCreateRequest request = new PaymentCreateRequest(orderId, payMtdCd, payAmount, idempotencyKey);

            when(jwtUtil.getCurrentMemberId()).thenReturn(memberId);
            when(memberService.validateActiveMemberByMemberId(memberId)).thenReturn(member);
            when(orderService.findById(orderId)).thenReturn(Optional.of(order));
            when(paymentRepository.existsByIdempotencyKey(idempotencyKey)).thenReturn(false);
            when(paymentRepository.findTopByOrderOrderIdOrderByPaymentIdDesc(orderId)).thenReturn(Optional.of(existingPayment));

            // When & Then
            assertThatThrownBy(() -> paymentService.createPayment(request))
                    .isInstanceOf(BizException.class)
                    .hasMessage(ResponseType.PAY_CANCELED.message());

            verify(jwtUtil).getCurrentMemberId();
            verify(memberService).validateActiveMemberByMemberId(memberId);
            verify(orderService).findById(orderId);
            verify(paymentRepository).existsByIdempotencyKey(idempotencyKey);
            verify(paymentRepository).findTopByOrderOrderIdOrderByPaymentIdDesc(orderId);
            verify(paymentRepository, never()).save(any());
        }

        @Test
        @DisplayName("DataIntegrityViolationException - 중복 키")
        void dataIntegrityViolationException_duplicateKey_throwsException() {
            // Given
            String memberId = "testuser123";
            Long orderId = 1L;
            String idempotencyKey = "test-key-123";
            String payMtdCd = "00";
            Integer payAmount = 10000;

            Member member = Member.builder()
                    .memberId(memberId)
                    .memberStsCd("00")
                    .regDt(LocalDateTime.now().toLocalDate())
                    .build();

            Orders order = Orders.builder()
                    .orderId(orderId)
                    .member(member)
                    .totalAmount(payAmount)
                    .orderStsCd(OrderStsCdConst.PAYMENT_PENDING)
                    .regDtm(LocalDateTime.now())
                    .build();

            PaymentCreateRequest request = new PaymentCreateRequest(orderId, payMtdCd, payAmount, idempotencyKey);

            when(jwtUtil.getCurrentMemberId()).thenReturn(memberId);
            when(memberService.validateActiveMemberByMemberId(memberId)).thenReturn(member);
            when(orderService.findById(orderId)).thenReturn(Optional.of(order));
            when(paymentRepository.existsByIdempotencyKey(idempotencyKey)).thenReturn(false);
            when(paymentRepository.findTopByOrderOrderIdOrderByPaymentIdDesc(orderId)).thenReturn(Optional.empty());
            when(paymentRepository.save(any(Payment.class))).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

            // When & Then
            assertThatThrownBy(() -> paymentService.createPayment(request))
                    .isInstanceOf(BizException.class)
                    .hasMessage(ResponseType.PAY_IN_PROGRESS.message());

            verify(jwtUtil).getCurrentMemberId();
            verify(memberService).validateActiveMemberByMemberId(memberId);
            verify(orderService).findById(orderId);
            verify(paymentRepository).existsByIdempotencyKey(idempotencyKey);
            verify(paymentRepository).findTopByOrderOrderIdOrderByPaymentIdDesc(orderId);
            verify(paymentRepository).save(any(Payment.class));
        }

        @Test
        @DisplayName("DataIntegrityViolationException - 기타 오류")
        void dataIntegrityViolationException_otherError_throwsException() {
            // Given
            String memberId = "testuser123";
            Long orderId = 1L;
            String idempotencyKey = "test-key-123";
            String payMtdCd = "00";
            Integer payAmount = 10000;

            Member member = Member.builder()
                    .memberId(memberId)
                    .memberStsCd("00")
                    .regDt(LocalDateTime.now().toLocalDate())
                    .build();

            Orders order = Orders.builder()
                    .orderId(orderId)
                    .member(member)
                    .totalAmount(payAmount)
                    .orderStsCd(OrderStsCdConst.PAYMENT_PENDING)
                    .regDtm(LocalDateTime.now())
                    .build();

            PaymentCreateRequest request = new PaymentCreateRequest(orderId, payMtdCd, payAmount, idempotencyKey);

            when(jwtUtil.getCurrentMemberId()).thenReturn(memberId);
            when(memberService.validateActiveMemberByMemberId(memberId)).thenReturn(member);
            when(orderService.findById(orderId)).thenReturn(Optional.of(order));
            when(paymentRepository.existsByIdempotencyKey(idempotencyKey)).thenReturn(false);
            when(paymentRepository.findTopByOrderOrderIdOrderByPaymentIdDesc(orderId)).thenReturn(Optional.empty());
            when(paymentRepository.save(any(Payment.class))).thenThrow(new DataIntegrityViolationException("Other error"));

            // When & Then
            assertThatThrownBy(() -> paymentService.createPayment(request))
                    .isInstanceOf(DataIntegrityViolationException.class);

            verify(jwtUtil).getCurrentMemberId();
            verify(memberService).validateActiveMemberByMemberId(memberId);
            verify(orderService).findById(orderId);
            verify(paymentRepository).existsByIdempotencyKey(idempotencyKey);
            verify(paymentRepository).findTopByOrderOrderIdOrderByPaymentIdDesc(orderId);
            verify(paymentRepository).save(any(Payment.class));
        }
    }

    @Nested
    @DisplayName("Mock 결제 테스트")
    class MockPaymentTest {

        @Test
        @DisplayName("MockPaymentUtil 호출 및 예외 처리")
        void success_mockPaymentUtilCallAndExceptionHandling() {
            // Given
            Payment payment = Payment.builder()
                    .paymentId(1L)
                    .payMtdCd("00")
                    .payAmount(10000)
                    .payStsCd(PayStsCdConst.PENDING)
                    .regDtm(LocalDateTime.now())
                    .build();

            // When
            String result = paymentService.mockPaymentTest(payment);

            // Then
            // MockPaymentUtil의 랜덤 결과에 따라 성공 또는 실패가 될 수 있음
            assertThat(result).isIn("Success", "결제오류");
        }
    }

    @Nested
    @DisplayName("결제 조회 테스트")
    class FindTopByOrderOrderIdOrderByPaymentIdDescTest {

        @Test
        @DisplayName("정상 케이스 - 결제가 존재하는 경우")
        void success_paymentExists() {
            // Given
            Long orderId = 1L;
            Payment payment = Payment.builder()
                    .paymentId(1L)
                    .payMtdCd("00")
                    .payAmount(10000)
                    .payStsCd(PayStsCdConst.SUCCESS)
                    .regDtm(LocalDateTime.now())
                    .build();

            when(paymentRepository.findTopByOrderOrderIdOrderByPaymentIdDesc(orderId)).thenReturn(Optional.of(payment));

            // When
            Optional<Payment> result = paymentService.findTopByOrderOrderIdOrderByPaymentIdDesc(orderId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getPaymentId()).isEqualTo(1L);
            assertThat(result.get().getPayStsCd()).isEqualTo(PayStsCdConst.SUCCESS);

            verify(paymentRepository).findTopByOrderOrderIdOrderByPaymentIdDesc(orderId);
        }

        @Test
        @DisplayName("결제가 존재하지 않는 경우")
        void success_paymentNotExists() {
            // Given
            Long orderId = 999L;

            when(paymentRepository.findTopByOrderOrderIdOrderByPaymentIdDesc(orderId)).thenReturn(Optional.empty());

            // When
            Optional<Payment> result = paymentService.findTopByOrderOrderIdOrderByPaymentIdDesc(orderId);

            // Then
            assertThat(result).isEmpty();

            verify(paymentRepository).findTopByOrderOrderIdOrderByPaymentIdDesc(orderId);
        }

        @Test
        @DisplayName("null 주문 ID")
        void success_nullOrderId() {
            // Given
            Long orderId = null;

            when(paymentRepository.findTopByOrderOrderIdOrderByPaymentIdDesc(orderId)).thenReturn(Optional.empty());

            // When
            Optional<Payment> result = paymentService.findTopByOrderOrderIdOrderByPaymentIdDesc(orderId);

            // Then
            assertThat(result).isEmpty();

            verify(paymentRepository).findTopByOrderOrderIdOrderByPaymentIdDesc(orderId);
        }
    }

    @Nested
    @DisplayName("결제 저장 테스트")
    class SaveTest {

        @Test
        @DisplayName("정상 케이스")
        void success() {
            // Given
            Payment payment = Payment.builder()
                    .paymentId(1L)
                    .payMtdCd("00")
                    .payAmount(10000)
                    .payStsCd(PayStsCdConst.SUCCESS)
                    .regDtm(LocalDateTime.now())
                    .build();

            when(paymentRepository.save(payment)).thenReturn(payment);

            // When
            Payment result = paymentService.save(payment);

            // Then
            assertThat(result).isEqualTo(payment);
            assertThat(result.getPaymentId()).isEqualTo(1L);
            assertThat(result.getPayStsCd()).isEqualTo(PayStsCdConst.SUCCESS);

            verify(paymentRepository).save(payment);
        }

        @Test
        @DisplayName("null 결제 객체")
        void success_nullPayment() {
            // Given
            Payment payment = null;

            when(paymentRepository.save(payment)).thenReturn(null);

            // When
            Payment result = paymentService.save(payment);

            // Then
            assertThat(result).isNull();

            verify(paymentRepository).save(payment);
        }
    }
}
