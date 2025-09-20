package com.cafe.mobile.shcafe.order.controller;

import com.cafe.mobile.shcafe.common.model.AppResponse;
import com.cafe.mobile.shcafe.common.type.ResponseType;
import com.cafe.mobile.shcafe.order.dto.request.OrderCreateRequest;
import com.cafe.mobile.shcafe.order.dto.response.OrderCreateResponse;
import com.cafe.mobile.shcafe.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/order")
@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 주문 생성
    @PostMapping
    public ResponseEntity<AppResponse<OrderCreateResponse>> createOrder(@RequestBody @Valid OrderCreateRequest request) {
        OrderCreateResponse order = orderService.createOrder(request);

        return ResponseEntity.ok(AppResponse.<OrderCreateResponse>builder()
                .code(ResponseType.SUCCESS.code()).message(ResponseType.SUCCESS.message())
                .data(order)
                .build());
    }

    /*
    TODO:
     주문상태 업데이트(필요)
     주문정보이력테이블 작성
     회원의 주문리스트 조회
     주문상세조회 기능
     */
}
