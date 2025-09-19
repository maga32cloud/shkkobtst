package com.cafe.mobile.shcafe.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "category")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long categoryId; // 카테고리ID

    @Column(nullable = false)
    private String categoryName; // 카테고리명

    @Column()
    @ColumnDefault("0")
    private Integer displayOrder; // 순서

    @Column(nullable = false)
    @ColumnDefault("'Y'")
    private String useYn; // 사용여부

    @Column(nullable = false, updatable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime regDtm; // 등록일시
}
