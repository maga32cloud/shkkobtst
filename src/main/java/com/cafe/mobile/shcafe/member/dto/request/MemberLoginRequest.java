package com.cafe.mobile.shcafe.member.dto.request;

import com.cafe.mobile.shcafe.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 로그인 요청")
public class MemberLoginRequest {
    @NotBlank(message = "아이디는 필수입니다.")
    @Schema(description = "회원 아이디", example = "testuser123", required = true)
    private String memberId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Schema(description = "비밀번호", example = "password123!", required = true)
    private String password;

    // DTO - Entity 변환
    public Member toEntity(String encodedPassword) {
        return Member.builder()
                .memberId(memberId)
                .password(encodedPassword)
                .build();
    }
}
