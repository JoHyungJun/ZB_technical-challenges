package zeobase.ZB_technical.challenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zeobase.ZB_technical.challenges.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByPhone(String phone);
    Optional<Member> findByMemberId(String memberId);
}
