package com.cafe.mobile.shcafe.member.controller;

import com.cafe.mobile.shcafe.common.model.AppResponse;
import com.cafe.mobile.shcafe.common.type.ResponseType;
import com.cafe.mobile.shcafe.member.dto.MemberSignUpRequest;
import com.cafe.mobile.shcafe.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/member")
@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<AppResponse<Boolean>> signUp(@RequestBody MemberSignUpRequest request) {

        memberService.signUp(request);

        return ResponseEntity.ok(AppResponse.<Boolean>builder()
                .code(ResponseType.SUCCESS.code()).message(ResponseType.SUCCESS.message())
                .data(true).build());
    }
}
