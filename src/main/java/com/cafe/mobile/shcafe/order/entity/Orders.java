package com.cafe.mobile.shcafe.order.entity;

import com.cafe.mobile.shcafe.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Builder
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long orderId; // 주문ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 회원

    @Column()
    private String pickupNo; // 픽업번호

    @Setter
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer totalAmount; // 총금액

    @Column(nullable = false)
    @ColumnDefault("'00'")
    private String orderStsCd; // 주문상태코드

    @Column(nullable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime modDtm; // 수정일시

    @Column(nullable = false, updatable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime regDtm; // 등록일시

    // OrderItem 저장용 연관관계 정의방식
    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // OrderItem 연관관계 저장용 메소드
    public void add(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

}
