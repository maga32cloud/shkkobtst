package com.cafe.mobile.shcafe.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;

@Entity
@Table(name = "MEMBER",
        uniqueConstraints = {
                @UniqueConstraint(name = "EMAIL_UNIQUE", columnNames = "EMAIL"),
                @UniqueConstraint(name = "MEM_ID_UNIQUE", columnNames = "MEM_ID")
        })
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @Column(name = "MEM_ID", nullable = false)
    private String memId; // 회원ID (PK)

    @Column(name = "PWD", nullable = false)
    private String pwd; // 비밀번호

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email; // 이메일

    @Column(name = "NAME", nullable = false)
    private String name; // 이름

    @Column(name = "TEL_NO", nullable = false)
    private String telNo; // 전화번호

    @Column(name = "GENDER", nullable = false)
    private String gender; // 성별

    @Column(name = "BIRTH_DT", nullable = false)
    private Date birthDt; // 생년월일

    @Column(name = "REG_DT", nullable = false)
    private Date regDt; // 가입일자

    @Column(name = "CLS_DT")
    private Date clsDt; // 탈퇴일자 (nullable)

    @Column(name = "MEM_STS_CD", nullable = false)
    private String memStsCd; // 회원상태코드 (default '00')

    @PrePersist
    public void prePersist() {
        if (regDt == null) {
            regDt = Date.valueOf(LocalDate.now()); // 가입일자 기본값
        }
        if (memStsCd == null) {
            memStsCd = "00";
        }
    }
}