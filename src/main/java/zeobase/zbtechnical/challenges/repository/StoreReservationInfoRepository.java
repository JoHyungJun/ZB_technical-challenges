package zeobase.zbtechnical.challenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zeobase.zbtechnical.challenges.entity.StoreReservationInfo;

public interface StoreReservationInfoRepository extends JpaRepository<StoreReservationInfo, Long> {
}
