package zeobase.zbtechnical.challenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zeobase.zbtechnical.challenges.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByMemberId(Long memberId);
    void deleteByMemberId(Long memberId);
}
