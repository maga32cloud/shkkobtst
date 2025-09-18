package com.cafe.mobile.shcafe.member.dto.request;

import com.cafe.mobile.shcafe.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginRequest {
    @NotBlank(message = "아이디는 필수입니다.")
    private String memberId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    // DTO - Entity 변환
    public Member toEntity(String encodedPassword) {
        return Member.builder()
                .memberId(memberId)
                .password(encodedPassword)
                .build();
    }
}
