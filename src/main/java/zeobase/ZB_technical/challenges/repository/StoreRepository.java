package zeobase.ZB_technical.challenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zeobase.ZB_technical.challenges.entity.Member;
import zeobase.ZB_technical.challenges.entity.Store;

import java.util.Optional;

/**
 * 매장 관련 JpaRepository 인터페이스
 */
public interface StoreRepository extends JpaRepository<Store, Long> {

    boolean existsById(Long id);
}
