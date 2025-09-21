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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/member")
@RestController
@Tag(name = "회원 관리", description = "회원 가입, 로그인, 탈퇴 관련 API")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 회원")
    })
    @PostMapping
    public ResponseEntity<AppResponse<MemberSignUpResponse>> signUp(@RequestBody @Valid MemberSignUpRequest request) {
        MemberSignUpResponse member = memberService.signUp(request);

        return ResponseEntity.ok(AppResponse.<MemberSignUpResponse>builder()
                .code(ResponseType.SUCCESS.code()).message(ResponseType.SUCCESS.message()).data(member)
                .build());
    }

    @Operation(summary = "로그인", description = "회원 로그인을 수행하고 JWT 토큰을 반환합니다. 헤더로 내려온 토큰을 swagger 위쪽 Authorize에 넣으시면 쉽게 테스트가 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<AppResponse<Void>> login(@RequestBody @Valid MemberLoginRequest request) {
        String accessToken = memberService.login(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .body(AppResponse.<Void>builder().code(ResponseType.SUCCESS.code()).message(ResponseType.SUCCESS.message())
                .build());
    }

    @Operation(summary = "탈퇴 신청", description = "회원 탈퇴를 신청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "탈퇴 신청 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    @PutMapping("/withdraw/{memberId}")
    public ResponseEntity<AppResponse<MemberWithdrawResponse>> withdraw(
            @Parameter(description = "회원 ID", required = true) @PathVariable String memberId, 
            @RequestBody @Valid MemberWithdrawRequest request) {
        MemberWithdrawResponse member = memberService.withdraw(memberId, request);

        return ResponseEntity.ok(AppResponse.<MemberWithdrawResponse>builder()
                .code(ResponseType.SUCCESS.code()).message(ResponseType.SUCCESS.message()).data(member)
                .build());
    }

    @Operation(summary = "탈퇴 철회", description = "회원 탈퇴 신청을 철회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "탈퇴 철회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    @PutMapping("/cancelWithdraw/{memberId}")
    public ResponseEntity<AppResponse<Void>> cancelWithdraw(
            @Parameter(description = "회원 ID", required = true) @PathVariable String memberId, 
            @RequestBody @Valid MemberCancelWithdrawRequest request) {
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
