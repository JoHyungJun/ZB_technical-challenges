package zeobase.ZB_technical.challenges.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import zeobase.ZB_technical.challenges.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
