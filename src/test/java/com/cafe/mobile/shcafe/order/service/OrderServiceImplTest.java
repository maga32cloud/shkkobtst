package com.cafe.mobile.shcafe.order.service;

import com.cafe.mobile.shcafe.common.exception.BizException;
import com.cafe.mobile.shcafe.common.jwt.JwtUtil;
import com.cafe.mobile.shcafe.common.type.OrderStsCdConst;
import com.cafe.mobile.shcafe.common.type.ResponseType;
import com.cafe.mobile.shcafe.member.entity.Member;
import com.cafe.mobile.shcafe.member.service.MemberService;
import com.cafe.mobile.shcafe.order.dto.request.OrderCreateRequest;
import com.cafe.mobile.shcafe.order.dto.response.OrderCreateResponse;
import com.cafe.mobile.shcafe.order.entity.OrderItem;
import com.cafe.mobile.shcafe.order.entity.Orders;
import com.cafe.mobile.shcafe.order.event.OrderCancelledEvent;
import com.cafe.mobile.shcafe.order.repository.OrderRepository;
import com.cafe.mobile.shcafe.product.entity.Category;
import com.cafe.mobile.shcafe.product.entity.Product;
import com.cafe.mobile.shcafe.product.entity.ProductHistory;
import com.cafe.mobile.shcafe.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService 테스트")
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private ProductService productService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Nested
    @DisplayName("주문 생성 테스트")
    class CreateOrderTest {

        @Test
        @DisplayName("정상 케이스 - 단일 상품 주문")
        void success_singleProduct() {
            // Given
            String memberId = "testuser123";
            Member member = Member.builder()
                    .memberId(memberId)
                    .memberStsCd("00")
                    .regDt(LocalDateTime.now().toLocalDate())
                    .build();

            Category category = Category.builder()
                    .categoryId(1L)
                    .categoryName("커피")
                    .build();

            Product product = Product.builder()
                    .productId(1L)
                    .category(category)
                    .productName("아메리카노")
                    .price(4000)
                    .useYn("Y")
                    .regDtm(LocalDateTime.now())
                    .build();

            OrderCreateRequest.OrderItemRequest itemRequest = new OrderCreateRequest.OrderItemRequest(1L, 2, 4000);
            OrderCreateRequest request = new OrderCreateRequest(List.of(itemRequest));

            Orders savedOrder = Orders.builder()
                    .orderId(1L)
                    .member(member)
                    .totalAmount(8000)
                    .orderStsCd(OrderStsCdConst.PAYMENT_PENDING)
                    .regDtm(LocalDateTime.now())
                    .build();

            when(jwtUtil.getCurrentMemberId()).thenReturn(memberId);
            when(memberService.validateActiveMemberByMemberId(memberId)).thenReturn(member);
            when(productService.findByProductIdAndUseYn(1L, "Y")).thenReturn(Optional.of(product));
            when(productService.findProductInfoAtTimeUseYn(eq(1L), any(LocalDateTime.class), eq("Y")))
                    .thenReturn(Optional.empty()); // ProductHistory 없음
            when(orderRepository.save(any(Orders.class))).thenAnswer(invocation -> {
                Orders order = invocation.getArgument(0);
                // 실제로는 DB에서 ID가 생성되지만, 테스트에서는 수동으로 설정
                // Reflection을 사용해서 orderId 설정
                try {
                    java.lang.reflect.Field field = Orders.class.getDeclaredField("orderId");
                    field.setAccessible(true);
                    field.set(order, 1L);
                } catch (Exception e) {
                    // Reflection 실패시 미리 정의된 객체 반환
                    return savedOrder;
                }
                return order;
            });

            // When
            OrderCreateResponse response = orderService.createOrder(request);

            // Then
            assertThat(response.getOrderId()).isEqualTo(1L);

            verify(jwtUtil).getCurrentMemberId();
            verify(memberService).validateActiveMemberByMemberId(memberId);
            verify(productService).findByProductIdAndUseYn(1L, "Y");
            verify(productService).findProductInfoAtTimeUseYn(eq(1L), any(LocalDateTime.class), eq("Y"));
            verify(orderRepository).save(any(Orders.class));
        }

        @Test
        @DisplayName("정상 케이스 - 다중 상품 주문")
        void success_multipleProducts() {
            // Given
            String memberId = "testuser123";
            Member member = Member.builder()
                    .memberId(memberId)
                    .memberStsCd("00")
                    .regDt(LocalDateTime.now().toLocalDate())
                    .build();

            Category category = Category.builder()
                    .categoryId(1L)
                    .categoryName("커피")
                    .build();

            Product product1 = Product.builder()
                    .productId(1L)
                    .category(category)
                    .productName("아메리카노")
                    .price(4000)
                    .useYn("Y")
                    .regDtm(LocalDateTime.now())
                    .build();

            Product product2 = Product.builder()
                    .productId(2L)
                    .category(category)
                    .productName("라떼")
                    .price(4500)
                    .useYn("Y")
                    .regDtm(LocalDateTime.now())
                    .build();

            OrderCreateRequest.OrderItemRequest item1 = new OrderCreateRequest.OrderItemRequest(1L, 2, 4000);
            OrderCreateRequest.OrderItemRequest item2 = new OrderCreateRequest.OrderItemRequest(2L, 1, 4500);
            OrderCreateRequest request = new OrderCreateRequest(List.of(item1, item2));

            Orders savedOrder = Orders.builder()
                    .orderId(1L)
                    .member(member)
                    .totalAmount(12500) // 4000*2 + 4500*1
                    .orderStsCd(OrderStsCdConst.PAYMENT_PENDING)
                    .regDtm(LocalDateTime.now())
                    .build();

            when(jwtUtil.getCurrentMemberId()).thenReturn(memberId);
            when(memberService.validateActiveMemberByMemberId(memberId)).thenReturn(member);
            when(productService.findByProductIdAndUseYn(1L, "Y")).thenReturn(Optional.of(product1));
            when(productService.findByProductIdAndUseYn(2L, "Y")).thenReturn(Optional.of(product2));
            when(productService.findProductInfoAtTimeUseYn(anyLong(), any(LocalDateTime.class), anyString())).thenReturn(Optional.empty());
            when(orderRepository.save(any(Orders.class))).thenAnswer(invocation -> {
                Orders order = invocation.getArgument(0);
                // 실제로는 DB에서 ID가 생성되지만, 테스트에서는 수동으로 설정
                // Reflection을 사용해서 orderId 설정
                try {
                    java.lang.reflect.Field field = Orders.class.getDeclaredField("orderId");
                    field.setAccessible(true);
                    field.set(order, 1L);
                } catch (Exception e) {
                    // Reflection 실패시 미리 정의된 객체 반환
                    return savedOrder;
                }
                return order;
            });

            // When
            OrderCreateResponse response = orderService.createOrder(request);

            // Then
            assertThat(response.getOrderId()).isEqualTo(1L);

            verify(productService).findByProductIdAndUseYn(1L, "Y");
            verify(productService).findByProductIdAndUseYn(2L, "Y");
            verify(orderRepository).save(any(Orders.class));
        }

        @Test
        @DisplayName("존재하지 않는 상품")
        void invalidProduct_throwsException() {
            // Given
            String memberId = "testuser123";
            Member member = Member.builder()
                    .memberId(memberId)
                    .memberStsCd("00")
                    .regDt(LocalDateTime.now().toLocalDate())
                    .build();

            OrderCreateRequest.OrderItemRequest itemRequest = new OrderCreateRequest.OrderItemRequest(999L, 1, 4000);
            OrderCreateRequest request = new OrderCreateRequest(List.of(itemRequest));

            when(jwtUtil.getCurrentMemberId()).thenReturn(memberId);
            when(memberService.validateActiveMemberByMemberId(memberId)).thenReturn(member);
            when(productService.findByProductIdAndUseYn(999L, "Y")).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> orderService.createOrder(request))
                    .isInstanceOf(BizException.class)
                    .hasMessage(ResponseType.INVALID_PRODUCT.message());

            verify(jwtUtil).getCurrentMemberId();
            verify(memberService).validateActiveMemberByMemberId(memberId);
            verify(productService).findByProductIdAndUseYn(999L, "Y");
            verify(orderRepository, never()).save(any());
        }

        @Test
        @DisplayName("가격 불일치 - ProductHistory 기준")
        void priceMismatch_withProductHistory_throwsException() {
            // Given
            String memberId = "testuser123";
            Member member = Member.builder()
                    .memberId(memberId)
                    .memberStsCd("00")
                    .regDt(LocalDateTime.now().toLocalDate())
                    .build();

            Category category = Category.builder()
                    .categoryId(1L)
                    .categoryName("커피")
                    .build();

            Product product = Product.builder()
                    .productId(1L)
                    .category(category)
                    .productName("아메리카노")
                    .price(4000) // 현재 가격
                    .useYn("Y")
                    .regDtm(LocalDateTime.now())
                    .build();

            ProductHistory productHistory = ProductHistory.builder()
                    .historyId(1L)
                    .productId(1L)
                    .price(4500) // 과거 가격 (다름)
                    .useYn("Y")
                    .regDtm(LocalDateTime.now().minusDays(1))
                    .build();

            OrderCreateRequest.OrderItemRequest itemRequest = new OrderCreateRequest.OrderItemRequest(1L, 1, 4000); // 요청 가격
            OrderCreateRequest request = new OrderCreateRequest(List.of(itemRequest));

            when(jwtUtil.getCurrentMemberId()).thenReturn(memberId);
            when(memberService.validateActiveMemberByMemberId(memberId)).thenReturn(member);
            when(productService.findByProductIdAndUseYn(1L, "Y")).thenReturn(Optional.of(product));
            when(productService.findProductInfoAtTimeUseYn(eq(1L), any(LocalDateTime.class), eq("Y")))
                    .thenReturn(Optional.of(productHistory));

            // When & Then
            assertThatThrownBy(() -> orderService.createOrder(request))
                    .isInstanceOf(BizException.class)
                    .hasMessage(ResponseType.ORDER_PRICE_MISMATCH.message());

            verify(jwtUtil).getCurrentMemberId();
            verify(memberService).validateActiveMemberByMemberId(memberId);
            verify(productService).findByProductIdAndUseYn(1L, "Y");
            verify(productService).findProductInfoAtTimeUseYn(eq(1L), any(LocalDateTime.class), eq("Y"));
            verify(orderRepository, never()).save(any());
        }

        @Test
        @DisplayName("가격 불일치 - 현재 상품 가격 기준")
        void priceMismatch_withCurrentProduct_throwsException() {
            // Given
            String memberId = "testuser123";
            Member member = Member.builder()
                    .memberId(memberId)
                    .memberStsCd("00")
                    .regDt(LocalDateTime.now().toLocalDate())
                    .build();

            Category category = Category.builder()
                    .categoryId(1L)
                    .categoryName("커피")
                    .build();

            Product product = Product.builder()
                    .productId(1L)
                    .category(category)
                    .productName("아메리카노")
                    .price(4000) // 현재 가격
                    .useYn("Y")
                    .regDtm(LocalDateTime.now())
                    .build();

            OrderCreateRequest.OrderItemRequest itemRequest = new OrderCreateRequest.OrderItemRequest(1L, 1, 4500); // 다른 가격
            OrderCreateRequest request = new OrderCreateRequest(List.of(itemRequest));

            when(jwtUtil.getCurrentMemberId()).thenReturn(memberId);
            when(memberService.validateActiveMemberByMemberId(memberId)).thenReturn(member);
            when(productService.findByProductIdAndUseYn(1L, "Y")).thenReturn(Optional.of(product));
            when(productService.findProductInfoAtTimeUseYn(eq(1L), any(LocalDateTime.class), eq("Y")))
                    .thenReturn(Optional.empty()); // ProductHistory 없음

            // When & Then
            assertThatThrownBy(() -> orderService.createOrder(request))
                    .isInstanceOf(BizException.class)
                    .hasMessage(ResponseType.ORDER_PRICE_MISMATCH.message());

            verify(jwtUtil).getCurrentMemberId();
            verify(memberService).validateActiveMemberByMemberId(memberId);
            verify(productService).findByProductIdAndUseYn(1L, "Y");
            verify(productService).findProductInfoAtTimeUseYn(eq(1L), any(LocalDateTime.class), eq("Y"));
            verify(orderRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("주문 조회 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("정상 케이스 - 주문이 존재하는 경우")
        void success_orderExists() {
            // Given
            Long orderId = 1L;
            Member member = Member.builder()
                    .memberId("testuser123")
                    .build();

            Orders order = Orders.builder()
                    .orderId(orderId)
                    .member(member)
                    .totalAmount(8000)
                    .orderStsCd(OrderStsCdConst.PAYMENT_PENDING)
                    .regDtm(LocalDateTime.now())
                    .build();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // When
            Optional<Orders> result = orderService.findById(orderId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getOrderId()).isEqualTo(orderId);
            assertThat(result.get().getTotalAmount()).isEqualTo(8000);

            verify(orderRepository).findById(orderId);
        }

        @Test
        @DisplayName("주문이 존재하지 않는 경우")
        void success_orderNotExists() {
            // Given
            Long orderId = 999L;

            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            // When
            Optional<Orders> result = orderService.findById(orderId);

            // Then
            assertThat(result).isEmpty();

            verify(orderRepository).findById(orderId);
        }

        @Test
        @DisplayName("null 주문 ID")
        void success_nullOrderId() {
            // Given
            Long orderId = null;

            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            // When
            Optional<Orders> result = orderService.findById(orderId);

            // Then
            assertThat(result).isEmpty();

            verify(orderRepository).findById(orderId);
        }
    }

    @Nested
    @DisplayName("주문 상태 변경 테스트")
    class TransOrderStatusTest {

        @Test
        @DisplayName("정상 케이스 - 주문이 존재하는 경우")
        void success_orderExists() {
            // Given
            Long orderId = 1L;
            String newStatus = OrderStsCdConst.ORDER_RECEIVED;

            Member member = Member.builder()
                    .memberId("testuser123")
                    .build();

            Orders order = Orders.builder()
                    .orderId(orderId)
                    .member(member)
                    .totalAmount(8000)
                    .orderStsCd(OrderStsCdConst.PAYMENT_PENDING)
                    .regDtm(LocalDateTime.now())
                    .build();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // When
            orderService.transOrderStatus(orderId, newStatus);

            // Then
            assertThat(order.getOrderStsCd()).isEqualTo(newStatus);
            verify(orderRepository).findById(orderId);
        }

        @Test
        @DisplayName("주문이 존재하지 않는 경우")
        void success_orderNotExists() {
            // Given
            Long orderId = 999L;
            String newStatus = OrderStsCdConst.ORDER_RECEIVED;

            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            // When
            orderService.transOrderStatus(orderId, newStatus);

            // Then
            verify(orderRepository).findById(orderId);
            // 로그만 출력되고 예외는 발생하지 않음
        }

        @Test
        @DisplayName("null 주문 ID")
        void success_nullOrderId() {
            // Given
            Long orderId = null;
            String newStatus = OrderStsCdConst.ORDER_RECEIVED;

            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            // When
            orderService.transOrderStatus(orderId, newStatus);

            // Then
            verify(orderRepository).findById(orderId);
        }
    }

    @Nested
    @DisplayName("주문 취소 테스트")
    class CancelOrderTest {

        @Test
        @DisplayName("정상 케이스 - 결제 대기 상태 주문 취소")
        void success_paymentPending() {
            // Given
            Long orderId = 1L;
            String currentMemberId = "testuser123";

            Member member = Member.builder()
                    .memberId(currentMemberId)
                    .build();

            Orders order = Orders.builder()
                    .orderId(orderId)
                    .member(member)
                    .totalAmount(8000)
                    .orderStsCd(OrderStsCdConst.PAYMENT_PENDING) // 결제 대기
                    .regDtm(LocalDateTime.now())
                    .build();

            when(jwtUtil.getCurrentMemberId()).thenReturn(currentMemberId);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Orders.class))).thenReturn(order);

            // When
            orderService.cancelOrder(orderId);

            // Then
            assertThat(order.getOrderStsCd()).isEqualTo(OrderStsCdConst.USER_CANCELLED);
            verify(jwtUtil).getCurrentMemberId();
            verify(orderRepository).findById(orderId);
            verify(orderRepository).save(order);
            verify(eventPublisher, never()).publishEvent(any()); // 결제되지 않았으므로 이벤트 발행 안함
        }

        @Test
        @DisplayName("정상 케이스 - 결제 완료 상태 주문 취소")
        void success_paidOrder() {
            // Given
            Long orderId = 1L;
            String currentMemberId = "testuser123";

            Member member = Member.builder()
                    .memberId(currentMemberId)
                    .build();

            Orders order = Orders.builder()
                    .orderId(orderId)
                    .member(member)
                    .totalAmount(8000)
                    .orderStsCd(OrderStsCdConst.ORDER_RECEIVED) // 결제 완료
                    .regDtm(LocalDateTime.now())
                    .build();

            when(jwtUtil.getCurrentMemberId()).thenReturn(currentMemberId);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Orders.class))).thenReturn(order);

            // When
            orderService.cancelOrder(orderId);

            // Then
            assertThat(order.getOrderStsCd()).isEqualTo(OrderStsCdConst.USER_CANCELLED);
            verify(jwtUtil).getCurrentMemberId();
            verify(orderRepository).findById(orderId);
            verify(orderRepository).save(order);
            verify(eventPublisher).publishEvent(any(OrderCancelledEvent.class)); // 결제되었으므로 이벤트 발행
        }

        @Test
        @DisplayName("존재하지 않는 주문")
        void orderNotExists_throwsException() {
            // Given
            Long orderId = 999L;
            String currentMemberId = "testuser123";

            when(jwtUtil.getCurrentMemberId()).thenReturn(currentMemberId);
            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> orderService.cancelOrder(orderId))
                    .isInstanceOf(BizException.class)
                    .hasMessage(ResponseType.INVALID_ORDER.message());

            verify(jwtUtil).getCurrentMemberId();
            verify(orderRepository).findById(orderId);
            verify(orderRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("본인이 아닌 주문 취소")
        void notOwnOrder_throwsException() {
            // Given
            Long orderId = 1L;
            String currentMemberId = "testuser123";
            String orderOwnerId = "otheruser";

            Member member = Member.builder()
                    .memberId(orderOwnerId)
                    .build();

            Orders order = Orders.builder()
                    .orderId(orderId)
                    .member(member)
                    .totalAmount(8000)
                    .orderStsCd(OrderStsCdConst.PAYMENT_PENDING)
                    .regDtm(LocalDateTime.now())
                    .build();

            when(jwtUtil.getCurrentMemberId()).thenReturn(currentMemberId);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // When & Then
            assertThatThrownBy(() -> orderService.cancelOrder(orderId))
                    .isInstanceOf(BizException.class)
                    .hasMessage(ResponseType.NOT_ALLOWED.message());

            verify(jwtUtil).getCurrentMemberId();
            verify(orderRepository).findById(orderId);
            verify(orderRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("이미 취소된 주문")
        void alreadyCancelled_throwsException() {
            // Given
            Long orderId = 1L;
            String currentMemberId = "testuser123";

            Member member = Member.builder()
                    .memberId(currentMemberId)
                    .build();

            Orders order = Orders.builder()
                    .orderId(orderId)
                    .member(member)
                    .totalAmount(8000)
                    .orderStsCd(OrderStsCdConst.USER_CANCELLED) // 이미 취소됨
                    .regDtm(LocalDateTime.now())
                    .build();

            when(jwtUtil.getCurrentMemberId()).thenReturn(currentMemberId);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // When & Then
            assertThatThrownBy(() -> orderService.cancelOrder(orderId))
                    .isInstanceOf(BizException.class)
                    .hasMessage(ResponseType.ALREADY_CANCELLED.message());

            verify(jwtUtil).getCurrentMemberId();
            verify(orderRepository).findById(orderId);
            verify(orderRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("매장에서 취소된 주문")
        void storeCancelled_throwsException() {
            // Given
            Long orderId = 1L;
            String currentMemberId = "testuser123";

            Member member = Member.builder()
                    .memberId(currentMemberId)
                    .build();

            Orders order = Orders.builder()
                    .orderId(orderId)
                    .member(member)
                    .totalAmount(8000)
                    .orderStsCd(OrderStsCdConst.STORE_CANCELLED) // 매장에서 취소됨
                    .regDtm(LocalDateTime.now())
                    .build();

            when(jwtUtil.getCurrentMemberId()).thenReturn(currentMemberId);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // When & Then
            assertThatThrownBy(() -> orderService.cancelOrder(orderId))
                    .isInstanceOf(BizException.class)
                    .hasMessage(ResponseType.ALREADY_CANCELLED.message());

            verify(jwtUtil).getCurrentMemberId();
            verify(orderRepository).findById(orderId);
            verify(orderRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("이미 픽업된 주문")
        void alreadyPickedUp_throwsException() {
            // Given
            Long orderId = 1L;
            String currentMemberId = "testuser123";

            Member member = Member.builder()
                    .memberId(currentMemberId)
                    .build();

            Orders order = Orders.builder()
                    .orderId(orderId)
                    .member(member)
                    .totalAmount(8000)
                    .orderStsCd(OrderStsCdConst.PICKED_UP) // 이미 픽업됨
                    .regDtm(LocalDateTime.now())
                    .build();

            when(jwtUtil.getCurrentMemberId()).thenReturn(currentMemberId);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // When & Then
            assertThatThrownBy(() -> orderService.cancelOrder(orderId))
                    .isInstanceOf(BizException.class)
                    .hasMessage(ResponseType.ALREADY_PICKED_UP.message());

            verify(jwtUtil).getCurrentMemberId();
            verify(orderRepository).findById(orderId);
            verify(orderRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }
    }
}
