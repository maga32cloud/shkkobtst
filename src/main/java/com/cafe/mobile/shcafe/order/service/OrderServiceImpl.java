package com.cafe.mobile.shcafe.order.service;

import com.cafe.mobile.shcafe.common.exception.BizException;
import com.cafe.mobile.shcafe.common.jwt.JwtUtil;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MemberService memberService;
    private final ProductService productService;
    private final JwtUtil jwtUtil;

    public OrderServiceImpl(OrderRepository orderRepository, MemberService memberService, ProductService productService, JwtUtil jwtUtil) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.memberService = memberService;
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
            throw new BizException(ResponseType.NOT_MATCHED_PRICE);
        }
    }
}
