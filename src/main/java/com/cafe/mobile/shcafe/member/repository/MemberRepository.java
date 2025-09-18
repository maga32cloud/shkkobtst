package com.cafe.mobile.shcafe.member.repository;

import com.cafe.mobile.shcafe.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    @Query("select m from Member m " +
            "where m.memberId = :memberId " +
            "or (m.memStsCd <> :memStsCd and (m.email = :email or m.telNo = :telNo))"
    )
    Optional<Member> findMemberNotResigned(@Param("memberId") String memberId, @Param("email") String email, @Param("telNo") String telNo, @Param("memStsCd") String memStsCd);

    Optional<Member> findByMemberIdAndMemStsCd(String memberId, String memStsCd);
}
