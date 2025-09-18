package com.cafe.mobile.shcafe.member.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class MemberWithdrawResponse {
    private String memberId;
    private LocalDate clsDt;
}
