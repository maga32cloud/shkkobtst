package com.cafe.mobile.shcafe.member.repository;

import com.cafe.mobile.shcafe.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    Member findByTelNo(String telNo);

    boolean existsByTelNo(String telNo);
}
