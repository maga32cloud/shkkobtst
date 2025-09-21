package com.cafe.mobile.shcafe.order.service;

import com.cafe.mobile.shcafe.order.dto.request.OrderCreateRequest;
import com.cafe.mobile.shcafe.order.dto.response.OrderCreateResponse;
import com.cafe.mobile.shcafe.order.entity.Orders;

import java.util.Optional;

public interface OrderService {
    
    OrderCreateResponse createOrder(OrderCreateRequest request);

    Optional<Orders> findById(Long orderId);

    void transOrderStatus(Long orderId, String orderStsCd);
}
