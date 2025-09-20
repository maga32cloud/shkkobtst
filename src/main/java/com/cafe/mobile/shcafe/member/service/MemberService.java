package com.cafe.mobile.shcafe.member.service;

import com.cafe.mobile.shcafe.member.dto.request.MemberCancelWithdrawRequest;
import com.cafe.mobile.shcafe.member.dto.request.MemberLoginRequest;
import com.cafe.mobile.shcafe.member.dto.request.MemberSignUpRequest;
import com.cafe.mobile.shcafe.member.dto.request.MemberWithdrawRequest;
import com.cafe.mobile.shcafe.member.dto.response.MemberSignUpResponse;
import com.cafe.mobile.shcafe.member.dto.response.MemberWithdrawResponse;
import com.cafe.mobile.shcafe.member.entity.Member;

public interface MemberService {
    MemberSignUpResponse signUp(MemberSignUpRequest dto);

    String login(MemberLoginRequest dto);

    MemberWithdrawResponse withdraw(String memberId, MemberWithdrawRequest request);

    void cancelWithdraw(String memberId, MemberCancelWithdrawRequest request);

    Member validateActiveMemberByMemberId(String memberId);
}
