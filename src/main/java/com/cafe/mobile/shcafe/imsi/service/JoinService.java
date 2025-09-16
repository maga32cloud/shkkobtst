package com.cafe.mobile.shcafe.imsi.service;

import com.cafe.mobile.shcafe.imsi.dto.JoinDTO;
import com.cafe.mobile.shcafe.imsi.entity.Members;
import com.cafe.mobile.shcafe.imsi.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinProcess(JoinDTO joinDTO) {
        String phoneNumber = joinDTO.getPhoneNumber();
        int isExist = memberRepository.existsOrRecentlyWithdrawn(phoneNumber);

        if(isExist > 0) {
            return;
        }

        Members members = Members.builder()
                .phoneNumber(phoneNumber)
                .name(joinDTO.getName())
                .gender(joinDTO.getGender())
                .birthdate(joinDTO.getBirthdate())
                .nickname(joinDTO.getNickname())
                .password(bCryptPasswordEncoder.encode(joinDTO.getPassword()))
                .role("ROLE_USER")
                .build();

        memberRepository.save(members);
    }
}
