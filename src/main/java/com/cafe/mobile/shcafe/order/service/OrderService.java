package com.cafe.mobile.shcafe.order.service;

import com.cafe.mobile.shcafe.order.dto.request.OrderCreateRequest;
import com.cafe.mobile.shcafe.order.dto.response.OrderCreateResponse;

public interface OrderService {
    
    OrderCreateResponse createOrder(OrderCreateRequest request);

}
