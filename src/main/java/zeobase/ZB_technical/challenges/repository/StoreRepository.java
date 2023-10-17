package zeobase.ZB_technical.challenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zeobase.ZB_technical.challenges.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {

}
