package zeobase.zbtechnical.challenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zeobase.zbtechnical.challenges.entity.Member;

import java.util.Optional;

/**
 * 이용자 관련 JpaRepository 인터페이스
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByPhone(String phone);
    Optional<Member> findByUID(String UID);
    boolean existsByUID(String UID);
    boolean existsByPhone(String phone);
}
