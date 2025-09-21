package com.cafe.mobile.shcafe.order.controller;

import com.cafe.mobile.shcafe.common.model.AppResponse;
import com.cafe.mobile.shcafe.common.type.ResponseType;
import com.cafe.mobile.shcafe.order.dto.request.OrderCreateRequest;
import com.cafe.mobile.shcafe.order.dto.response.OrderCreateResponse;
import com.cafe.mobile.shcafe.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/order")
@RestController
@Tag(name = "주문 관리", description = "주문 생성, 취소 관련 API")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "회원 또는 상품을 찾을 수 없음")
    })
    @PostMapping
    public ResponseEntity<AppResponse<OrderCreateResponse>> createOrder(@RequestBody @Valid OrderCreateRequest request) {
        OrderCreateResponse order = orderService.createOrder(request);

        return ResponseEntity.ok(AppResponse.<OrderCreateResponse>builder()
                .code(ResponseType.SUCCESS.code()).message(ResponseType.SUCCESS.message())
                .data(order)
                .build());
    }

    @Operation(summary = "주문 취소", description = "기존 주문을 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 취소 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
    })
    @DeleteMapping("/{orderId}")
    public ResponseEntity<AppResponse<Void>> cancelOrder(
            @Parameter(description = "주문 ID", required = true) @PathVariable Long orderId) {
        orderService.cancelOrder(orderId);

        return ResponseEntity.ok(AppResponse.<Void>builder()
                .code(ResponseType.SUCCESS.code()).message(ResponseType.SUCCESS.message())
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
