package com.cafe.mobile.shcafe.order.service;

import com.cafe.mobile.shcafe.order.dto.request.OrderCreateRequest;
import com.cafe.mobile.shcafe.order.dto.response.OrderCreateResponse;
import com.cafe.mobile.shcafe.order.entity.Orders;

import java.util.Optional;

public interface OrderService {
    
    /**
     * 주문 생성
     * @param request
     * @return OrderCreateResponse
     */
    OrderCreateResponse createOrder(OrderCreateRequest request);

    /**
     * 주문 ID로 주문 조회
     * @param orderId
     * @return Optional<Orders>
     */
    Optional<Orders> findById(Long orderId);

    /**
     * 주문 상태 변경
     * @param orderId
     * @param orderStsCd
     */
    void transOrderStatus(Long orderId, String orderStsCd);

    /**
     * 주문 취소
     * @param orderId
     */
    void cancelOrder(Long orderId);
}
