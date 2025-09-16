package com.cafe.mobile.shcafe.imsi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.sql.Date;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Members {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String phoneNumber;

    private String password;

    private String nickname;

    private String name;

    private String gender;

    private String birthdate;

    private String role;

    private String useYn;

    private Date withdrawnDate;


    @Builder
    public Members(String phoneNumber, String password, String nickname, String name, String gender, String birthdate, String role) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.gender = gender;
        this.birthdate = birthdate;
        this.role = role;
    }
}
