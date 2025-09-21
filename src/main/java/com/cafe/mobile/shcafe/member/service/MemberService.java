package com.cafe.mobile.shcafe.member.service;

import com.cafe.mobile.shcafe.member.dto.request.MemberCancelWithdrawRequest;
import com.cafe.mobile.shcafe.member.dto.request.MemberLoginRequest;
import com.cafe.mobile.shcafe.member.dto.request.MemberSignUpRequest;
import com.cafe.mobile.shcafe.member.dto.request.MemberWithdrawRequest;
import com.cafe.mobile.shcafe.member.dto.response.MemberSignUpResponse;
import com.cafe.mobile.shcafe.member.dto.response.MemberWithdrawResponse;
import com.cafe.mobile.shcafe.member.entity.Member;

public interface MemberService {
    
    /**
     * 회원가입
     * @param dto
     * @return MemberSignUpResponse
     */
    MemberSignUpResponse signUp(MemberSignUpRequest dto);

    /**
     * 회원 로그인
     * @param dto 로그인 요청 정보
     * @return JWT 토큰 문자열
     */
    String login(MemberLoginRequest dto);

    /**
     * 회원 탈퇴신청
     * @param memberId
     * @param request
     * @return MemberWithdrawResponse
     */
    MemberWithdrawResponse withdraw(String memberId, MemberWithdrawRequest request);

    /**
     * 회원 탈퇴신청 취소
     * @param memberId
     * @param request
     */
    void cancelWithdraw(String memberId, MemberCancelWithdrawRequest request);

    /**
     * 회원 ID로 정상 상태 회원을 검증
     * @param memberId
     * @return Member
     */
    Member validateActiveMemberByMemberId(String memberId);
}
