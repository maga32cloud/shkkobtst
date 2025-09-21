package com.cafe.mobile.shcafe.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 탈퇴 철회 요청")
public class MemberCancelWithdrawRequest {
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Schema(description = "현재 비밀번호 (탈퇴 철회 확인용)", example = "password123!", required = true)
    private String password;
}