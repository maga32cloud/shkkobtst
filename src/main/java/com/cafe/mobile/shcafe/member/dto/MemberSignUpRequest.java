package com.cafe.mobile.shcafe.member.dto;

import com.cafe.mobile.shcafe.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignUpRequest {
    private String memId;
    private String pwd;
    private String email;
    private String name;
    private String telNo;
    private String gender;
    private Date birthDt;

    // DTO → Entity 변환 메서드
    public Member toEntity() {
        return Member.builder()
                .memId(memId)
                .pwd(pwd)
                .email(email)
                .name(name)
                .telNo(telNo)
                .gender(gender)
                .birthDt(birthDt)
                .regDt(Date.valueOf(LocalDate.now()))
                .build();
    }
}
