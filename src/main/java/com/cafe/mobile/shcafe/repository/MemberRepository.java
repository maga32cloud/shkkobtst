package com.cafe.mobile.shcafe.repository;

import com.cafe.mobile.shcafe.entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Members, Integer> {

    @Query(value = """
        select case when count(1) > 0 then 1 else 0 end
          from members
         where phone_number = :phoneNumber
           and (   use_yn = 'Y'
                or (    use_yn = 'N'
                    and datediff(sysdate(), str_to_date(withdrawn_date, '%Y%m%d')) < 30
                   )
               )
        """, nativeQuery = true)
    int existsOrRecentlyWithdrawn(@Param("phoneNumber") String phoneNumber);
}
