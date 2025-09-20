package com.cafe.mobile.shcafe.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_history")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Builder
public class ProductHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long historyId; // 히스토리ID

    @Column(nullable = false)
    private Long productId; // 상품ID

    @Column(nullable = false)
    private Long categoryId; // 카테고리ID

    @Column(nullable = false)
    private String productName; // 상품명

    @Column()
    private String description; // 상품설명

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer price; // 가격

    @Column()
    private Integer displayOrder; // 순서

    @Column()
    private String imgUrl; // 이미지URL

    @Column(nullable = false)
    @ColumnDefault("'Y'")
    private String useYn; // 사용여부

    @Column()
    private LocalDateTime clsDtm; // 종료일시

    @Column(nullable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime modDtm; // 수정일시

    @Column(nullable = false, updatable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime regDtm; // 등록일시
}
