package com.cafe.mobile.shcafe.member.service;

import com.cafe.mobile.shcafe.common.exception.AlreadyExistsException;
import com.cafe.mobile.shcafe.common.exception.BizException;
import com.cafe.mobile.shcafe.common.jwt.JwtUtil;
import com.cafe.mobile.shcafe.common.type.MemStsCdConst;
import com.cafe.mobile.shcafe.common.type.ResponseType;
import com.cafe.mobile.shcafe.member.dto.request.MemberCancelWithdrawRequest;
import com.cafe.mobile.shcafe.member.dto.request.MemberLoginRequest;
import com.cafe.mobile.shcafe.member.dto.request.MemberSignUpRequest;
import com.cafe.mobile.shcafe.member.dto.request.MemberWithdrawRequest;
import com.cafe.mobile.shcafe.member.dto.response.MemberSignUpResponse;
import com.cafe.mobile.shcafe.member.dto.response.MemberWithdrawResponse;
import com.cafe.mobile.shcafe.member.entity.Member;
import com.cafe.mobile.shcafe.member.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    MemberServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder, MemberRepository memberRepository, JwtUtil jwtUtil) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    // 회원가입
    @Override
    public MemberSignUpResponse signUp(MemberSignUpRequest request) {
        // 중복 체크
        checkDuplicatedMember(request);

        // 비밀번호 암호화
        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());
        Member member = request.toEntity(encodedPassword);

        memberRepository.save(member);

        return MemberSignUpResponse.builder()
                .memberId(request.getMemberId())
                .regDt(member.getRegDt())
                .build();
    }

    // 로그인
    @Override
    public String login(MemberLoginRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new BizException("ID가 일치하지 않습니다."));

        if (!bCryptPasswordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BizException("비밀번호가 일치하지 않습니다.");
        }

        String memStsCd = member.getMemStsCd();

        switch (memStsCd) {
            case MemStsCdConst.WITHDRAWN -> throw new BizException(ResponseType.WITHDRAWN_MEMBER); // 탈퇴 회원
            case MemStsCdConst.WITHDRAWING -> throw new BizException(ResponseType.WITHDRAWING_PROGRESS); // 탈퇴 진행중 회원
        }

        return jwtUtil.createJwt(member.getMemberId(), "USER");
    }

    // 탈퇴신청
    @Override
    @Transactional
    public MemberWithdrawResponse withdraw(String memberId, MemberWithdrawRequest request) {
        // 본인 여부 검증
        String currentMemberId = jwtUtil.getCurrentMemberId();
        if (!currentMemberId.equals(memberId)) {
            throw new BizException(ResponseType.NOT_ALLOWED);
        }

        // 정상 상태 멤버 불러오기
        Member member = memberRepository.findByMemberIdAndMemStsCd(memberId, MemStsCdConst.ACTIVE)
                .orElseThrow(() -> new BizException(ResponseType.BAD_REQUEST));

        // 비밀번호 검증
        if (!bCryptPasswordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BizException("비밀번호가 일치하지 않습니다.");
        }

        member.setMemStsCd(MemStsCdConst.WITHDRAWING);
        member.setClsDt(LocalDate.now());

        memberRepository.save(member);

        return MemberWithdrawResponse.builder()
                .memberId(memberId)
                .clsDt(member.getClsDt())
                .build();
    }

    // 탈퇴신청 취소
    @Override
    @Transactional
    public void cancelWithdraw(String memberId, MemberCancelWithdrawRequest request) {
        // 탈퇴대기 상태 멤버 불러오기
        Member member = memberRepository.findByMemberIdAndMemStsCd(memberId, MemStsCdConst.WITHDRAWING)
                .orElseThrow(() -> new BizException(ResponseType.BAD_REQUEST));

        // 비밀번호 검증
        if (!bCryptPasswordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BizException("비밀번호가 일치하지 않습니다.");
        }

        member.setMemStsCd(MemStsCdConst.ACTIVE);
        member.setClsDt(null);

        memberRepository.save(member);
    }


    // 회원가입 중복체크
    public void checkDuplicatedMember(MemberSignUpRequest request) {
        // 탈퇴 상태가 아닌 회원 정보 검색
        Optional<Member> duplicate = memberRepository.findMemberNotResigned(
                request.getMemberId(), request.getEmail(), request.getTelNo(), MemStsCdConst.WITHDRAWN
        );

        if (duplicate.isPresent()) {
            Member dup = duplicate.get();

            if (dup.getMemberId().equals(request.getMemberId())) {
                throw new AlreadyExistsException("ID");
            } else if (dup.getEmail().equals(request.getEmail())) {
                throw new AlreadyExistsException("이메일");
            } else if (dup.getTelNo().equals(request.getTelNo())) {
                throw new AlreadyExistsException("전화번호");
            }
        }
    }

}
