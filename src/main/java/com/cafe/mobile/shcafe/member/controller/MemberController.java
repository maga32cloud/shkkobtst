package com.cafe.mobile.shcafe.member.controller;

import com.cafe.mobile.shcafe.common.model.AppResponse;
import com.cafe.mobile.shcafe.common.type.ResponseType;
import com.cafe.mobile.shcafe.member.dto.request.MemberCancelWithdrawRequest;
import com.cafe.mobile.shcafe.member.dto.request.MemberLoginRequest;
import com.cafe.mobile.shcafe.member.dto.request.MemberSignUpRequest;
import com.cafe.mobile.shcafe.member.dto.request.MemberWithdrawRequest;
import com.cafe.mobile.shcafe.member.dto.response.MemberSignUpResponse;
import com.cafe.mobile.shcafe.member.dto.response.MemberWithdrawResponse;
import com.cafe.mobile.shcafe.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/member")
@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 회원가입
    @PostMapping
    public ResponseEntity<AppResponse<MemberSignUpResponse>> signUp(@RequestBody @Valid MemberSignUpRequest request) {

        MemberSignUpResponse member = memberService.signUp(request);

        return ResponseEntity.ok(AppResponse.<MemberSignUpResponse>builder()
                .code(ResponseType.SUCCESS.code()).message(ResponseType.SUCCESS.message()).data(member)
                .build());
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<AppResponse<Void>> login(@RequestBody @Valid MemberLoginRequest request) {
        String accessToken = memberService.login(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .body(AppResponse.<Void>builder().code(ResponseType.SUCCESS.code()).message(ResponseType.SUCCESS.message())
                .build());
    }

    // 탈퇴신청
    @PutMapping("/withdraw/{memberId}")
    public ResponseEntity<AppResponse<MemberWithdrawResponse>> withdraw(@PathVariable String memberId, @RequestBody @Valid MemberWithdrawRequest request) {
        MemberWithdrawResponse member = memberService.withdraw(memberId, request);

        return ResponseEntity.ok(AppResponse.<MemberWithdrawResponse>builder()
                .code(ResponseType.SUCCESS.code()).message(ResponseType.SUCCESS.message()).data(member)
                .build());
    }

    // 탈퇴 철회
    @PutMapping("/cancelWithdraw/{memberId}")
    public ResponseEntity<AppResponse<Void>> cancelWithdraw(@PathVariable String memberId, @RequestBody @Valid MemberCancelWithdrawRequest request) {
        memberService.cancelWithdraw(memberId, request);

        return ResponseEntity.ok(AppResponse.<Void>builder()
                .code(ResponseType.SUCCESS.code()).message(ResponseType.SUCCESS.message())
                .build());
    }

    /*
    TODO:
     사용자조회
     사용자업데이트 : 회원이력 작성
     */
}
