package com.cafe.mobile.shcafe.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long productId; // 상품ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category; // 카테고리

    @Column(nullable = false)
    private String productName; // 상품명

    @Column()
    private String description; // 상품설명

    @Column(nullable = false)
    private Integer price; // 가격

    @Column()
    private String displayOrder; // 순서

    @Column()
    private String imgUrl; // 이미지URL

    @Column(nullable = false)
    @ColumnDefault("'Y'")
    private String useYn; // 사용여부

    @Column(nullable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime modDtm; // 수정일시

    @Column(nullable = false, updatable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime regDtm; // 등록일시
}
