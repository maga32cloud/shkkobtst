package com.cafe.mobile.shcafe.order.service;

import com.cafe.mobile.shcafe.order.event.OrderCancelledEvent;
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
import com.cafe.mobile.shcafe.order.repository.OrderRepository;
import com.cafe.mobile.shcafe.product.entity.Product;
import com.cafe.mobile.shcafe.product.entity.ProductHistory;
import com.cafe.mobile.shcafe.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MemberService memberService;
    private final ProductService productService;
    private final JwtUtil jwtUtil;
    private final ApplicationEventPublisher eventPublisher;

    public OrderServiceImpl(OrderRepository orderRepository, MemberService memberService, ProductService productService, JwtUtil jwtUtil, ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.memberService = memberService;
        this.eventPublisher = eventPublisher;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public OrderCreateResponse createOrder(OrderCreateRequest request) {
        // Jwt ID로 회원 존재 여부 확인
        Member member = memberService.validateActiveMemberByMemberId(jwtUtil.getCurrentMemberId());

        // 주문시간 기록
        LocalDateTime orderTime = LocalDateTime.now();

        // 주문 생성
        Orders order = Orders.builder()
                .member(member)
                .modDtm(orderTime)
                .regDtm(orderTime)
                .build();

        int totalAmount = 0;
        for (OrderCreateRequest.OrderItemRequest itemRequest : request.getOrderItems()) {
            // 상품존재 및 현재 판매 여부 확인
            Product product = productService.findByProductIdAndUseYn(itemRequest.getProductId(), "Y")
                    .orElseThrow(() -> new BizException(ResponseType.INVALID_PRODUCT));

            // ProductHistory와 가격검증
            validateProductPrice(product, itemRequest.getPrice(), orderTime);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .price(itemRequest.getPrice())
                    .totalPrice(itemRequest.getPrice() * itemRequest.getQuantity())
                    .regDtm(orderTime)
                    .build();

            order.add(orderItem);
            totalAmount += orderItem.getTotalPrice();
        }

        // 총주문금액 설정
        order.setTotalAmount(totalAmount);

        // order저장시 orderItem도 함께 저장
        orderRepository.save(order);

        return OrderCreateResponse.builder()
                .orderId(order.getOrderId())
                .build();
    }

    // 주문 취소
    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        // JWT로 현재 사용자 확인
        String currentMemberId = jwtUtil.getCurrentMemberId();

        // 주문 존재 여부 확인 및 소유자 확인
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BizException(ResponseType.INVALID_ORDER));

        if (!(currentMemberId).equals(order.getMember().getMemberId())) {
            throw new BizException(ResponseType.NOT_ALLOWED);
        }

        // 결제여부
        boolean isPaid = true;

        // 주문 상태 확인 (이미 취소되었거나 픽업된 주문은 회원이 취소 불가)
        switch (order.getOrderStsCd()) {
            case OrderStsCdConst.PAYMENT_PENDING: isPaid = false;
                break;
            case OrderStsCdConst.USER_CANCELLED:
            case OrderStsCdConst.STORE_CANCELLED: throw new BizException(ResponseType.ALREADY_CANCELLED);
            case OrderStsCdConst.PICKED_UP: throw new BizException(ResponseType.ALREADY_PICKED_UP);
        }

        // 주문 상태를 취소로 변경
        order.setOrderStsCd(OrderStsCdConst.USER_CANCELLED);
        orderRepository.save(order);

        // 결제가 진행됐다면 취소 이벤트 발행
        if(isPaid) {
            eventPublisher.publishEvent(new OrderCancelledEvent(this, orderId, currentMemberId));
        }
    }

    // ID로 찾기
    @Override
    public Optional<Orders> findById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    // 주문상태변경
    @Override
    @Transactional
    public void transOrderStatus(Long orderId, String orderStsCd) {
        orderRepository.findById(orderId).ifPresentOrElse(
            orders -> orders.setOrderStsCd(orderStsCd),
            () -> log.warn("주문ID {} 이 존재하지 않아 상태 변경 실패", orderId) // TODO: 재시도 로직 작성
        );
    }

    // 상품 가격 검증: ProductHistory -> Product db와 비교
    private void validateProductPrice(Product product, Integer requestPrice, LocalDateTime orderTime) {
        Integer realPrice = 0;
        try {
            // 상품내역에서 해당 주문시점의 판매가능 가격 조회
            Optional<ProductHistory> priceHistory = productService.findProductInfoAtTimeUseYn(product.getProductId(), orderTime, "Y");

            // 상품내역에 없으면 현재상품 가격 적용
            realPrice = priceHistory.map(ProductHistory::getPrice)
                    .orElse(product.getPrice());
        } catch (Exception e) {
            // 내역관련 다른 예외 발생시에도 현재상품 가격 적용
            realPrice = product.getPrice();
        }

        if (!realPrice.equals(requestPrice)) {
            throw new BizException(ResponseType.ORDER_PRICE_MISMATCH);
        }
    }
}
