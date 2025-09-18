package com.cafe.mobile.shcafe.member.dto.request;

import com.cafe.mobile.shcafe.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignUpRequest {
    @NotBlank(message = "아이디는 필수입니다.")
    private String memberId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^[0-9]{9,12}$", message = "전화번호는 9~12자리 숫자여야 합니다.")
    private String telNo;

    @NotBlank(message = "성별은 필수입니다.")
    @Pattern(regexp = "^[MW]$", message = "성별은 M 또는 W만 입력 가능합니다.")
    private String gender;

    @JsonFormat(pattern = "yyyyMMdd")
    @NotNull(message = "생년월일은 필수입니다.")
    private LocalDate birthDt;

    // DTO - Entity 변환
    public Member toEntity(String encodedPassword) {
        return Member.builder()
                .memberId(memberId)
                .password(encodedPassword)
                .email(email)
                .name(name)
                .telNo(telNo)
                .gender(gender)
                .birthDt(birthDt)
                .regDt(LocalDate.now())
                .build();
    }
}
