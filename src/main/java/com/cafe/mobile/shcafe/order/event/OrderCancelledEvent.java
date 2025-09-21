package com.cafe.mobile.shcafe.order.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderCancelledEvent extends ApplicationEvent {
    
    private final Long orderId;    // 주문ID
    private final String memberId; // 회원ID
    
    public OrderCancelledEvent(Object source, Long orderId, String memberId) {
        super(source);
        this.orderId = orderId;
        this.memberId = memberId;
    }
}
