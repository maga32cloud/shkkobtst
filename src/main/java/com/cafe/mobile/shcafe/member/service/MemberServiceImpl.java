package com.cafe.mobile.shcafe.member.service;

import com.cafe.mobile.shcafe.member.dto.MemberSignUpRequest;
import com.cafe.mobile.shcafe.member.entity.Member;
import com.cafe.mobile.shcafe.member.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberRepository memberRepository;

    MemberServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder, MemberRepository memberRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.memberRepository = memberRepository;
    }

    public void signUp(MemberSignUpRequest request) {
        // 중복 체크
        if (memberRepository.existsByTelNo(request.getTelNo())) {
            throw new IllegalArgumentException("이미 가입된 전화번호입니다.");
        }

        Member member = Member.builder().memId(request.getMemId())
                .pwd(bCryptPasswordEncoder.encode(request.getPwd()))
                .email(request.getEmail())
                .name(request.getName())
                .telNo(request.getTelNo())
                .gender(request.getGender())
                .birthDt(request.getBirthDt())
                .build();
        memberRepository.save(member);
    };
}
