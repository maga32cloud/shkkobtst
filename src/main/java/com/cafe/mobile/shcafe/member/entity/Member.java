package com.cafe.mobile.shcafe.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;

@Entity
@Table(name = "member", uniqueConstraints = {
        @UniqueConstraint(name = "EMAIL_UNIQUE", columnNames = "email"),
        @UniqueConstraint(name = "MEM_ID_UNIQUE", columnNames = "mem_id")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Builder
public class Member {

    @Id
    @Column(nullable = false)
    private String memberId; // 회원ID

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false, unique = true)
    private String email; // 이메일

    @Column(nullable = false)
    private String name; // 이름

    @Column(nullable = false)
    private String telNo; // 전화번호

    @Column(nullable = false)
    private String gender; // 성별

    @Column(nullable = false)
    private LocalDate birthDt; // 생년월일

    @Column(nullable = false, updatable = false)
    private LocalDate regDt; // 가입일자

    @Column()
    @Setter
    private LocalDate clsDt; // 탈퇴일자

    @ColumnDefault("'00'")
    @Setter
    @Column(nullable = false)
    private String memStsCd; // 회원상태코드

}