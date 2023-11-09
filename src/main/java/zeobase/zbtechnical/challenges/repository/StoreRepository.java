package zeobase.zbtechnical.challenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zeobase.zbtechnical.challenges.entity.Store;

/**
 * 매장 관련 JpaRepository 인터페이스
 */
public interface StoreRepository extends JpaRepository<Store, Long> {

    boolean existsById(Long id);
}
