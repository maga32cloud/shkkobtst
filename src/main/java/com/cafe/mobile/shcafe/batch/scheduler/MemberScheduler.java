package com.cafe.mobile.shcafe.batch.scheduler;

import com.cafe.mobile.shcafe.common.type.MemberStsCdConst;
import com.cafe.mobile.shcafe.member.entity.Member;
import com.cafe.mobile.shcafe.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class MemberScheduler {

    private final MemberRepository memberRepository;

    public MemberScheduler(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // 매일 00:10분에 고객 탈퇴정보 업데이트
    @Scheduled(cron = "0 10 0 * * *")
    @Transactional
    public void withdrawClaimMember() {
        log.info("탈퇴처리 실행 시각: {}", LocalDateTime.now());

        try {
            LocalDate limitDays = LocalDate.now().minusDays(30);

            List<Member> expiredMembers = memberRepository.findWithdrawingMember(limitDays, MemberStsCdConst.WITHDRAWING);

            for (Member member : expiredMembers) {
                member.setMemberStsCd(MemberStsCdConst.WITHDRAWN); // 탈퇴 상태 갱신
                member.setClsDt(LocalDate.now()); // 최종 탈퇴일자 갱신
            }

            memberRepository.saveAll(expiredMembers);
            log.info("탈퇴완료 회원 수: {}", expiredMembers.size());
        } catch(Exception e) {
            log.warn("탈퇴처리 오류발생: ", e);
        } finally {
            log.info("탈퇴처리 완료 시각: {}", LocalDateTime.now());
        }
    }
}
