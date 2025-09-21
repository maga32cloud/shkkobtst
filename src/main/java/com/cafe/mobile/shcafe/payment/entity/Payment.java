package com.cafe.mobile.shcafe.payment.entity;

import com.cafe.mobile.shcafe.member.entity.Member;
import com.cafe.mobile.shcafe.order.entity.Orders;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long paymentId; // 결제ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true)
    private Orders order; // 주문

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 사용자

    @Column(unique = true)
    private String idempotencyKey; // 검증키

    @Column(nullable = false)
    private String payMtdCd; // 결제수단코드

    @Column(nullable = false)
    private Integer payAmount; // 결제금액

    @Setter
    @ColumnDefault("'00'")
    @Column(nullable = false)
    private String payStsCd; // 결제상태코드

    @Setter
    @Column()
    private String transactionId; // 거래ID

    @Setter
    @Column()
    private LocalDateTime paidDtm; // 결제일시

    @Setter
    @Column()
    private LocalDateTime cancelDtm; // 취소일시

    @Column(nullable = false, updatable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime regDtm; // 등록일시

    // pk, 검증키, 거래ID 제외 복사 메서드
    public Payment copy() {
        return Payment.builder()
                .order(this.order)
                .member(this.member)
                .payMtdCd(this.payMtdCd)
                .payAmount(this.payAmount)
                .payStsCd(this.payStsCd)
                .build();
    }
}