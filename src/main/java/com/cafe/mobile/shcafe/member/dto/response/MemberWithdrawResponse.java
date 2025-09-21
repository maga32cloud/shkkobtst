package com.cafe.mobile.shcafe.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
@Schema(description = "회원 탈퇴 응답")
public class MemberWithdrawResponse {
    @Schema(description = "회원 아이디", example = "testuser123")
    private String memberId;
    
    @Schema(description = "탈퇴 신청일", example = "2024-10-21")
    private LocalDate clsDt;
}
