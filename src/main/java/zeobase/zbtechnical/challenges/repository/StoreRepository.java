package zeobase.zbtechnical.challenges.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import zeobase.zbtechnical.challenges.dto.store.StoreInfoWithDistanceDiffDto;
import zeobase.zbtechnical.challenges.entity.Store;
import zeobase.zbtechnical.challenges.type.store.StoreSignedStatusType;
import zeobase.zbtechnical.challenges.type.store.StoreStatusType;

/**
 * 매장 관련 JpaRepository 인터페이스
 */
public interface StoreRepository extends JpaRepository<Store, Long> {

    boolean existsById(Long id);

    @Query(
    nativeQuery = true,
    value = "SELECT id AS storeId, name, explanation, status, latitude, longitude, " +
            "       open_hours AS openHours, closed_hours AS closedHours, star_rating AS starRating, " +
            "       (6371 * ACOS(cos(RADIANS(:latitude))" +
            "           * COS(RADIANS(latitude))" +
            "           * COS(RADIANS(longitude) - RADIANS(:longitude))" +
            "           + SIN(RADIANS(:latitude)) * SIN(RADIANS(longitude)))) AS `distanceDiff` " +
            "FROM store " +
            "WHERE signed_status = 'ACTIVE' " +
            "ORDER BY `distanceDiff` ",
    countQuery = "SELECT signed_status FROM store WHERE signed_status = 'ACTIVE';"
    )
    Page<StoreInfoWithDistanceDiffDto> findAllByActiveStoresAndDistanceDiffOrderByDistanceDiff(Double latitude, Double longitude, Pageable pageable);
    Page<Store> findAllByStatusAndSignedStatusOrderByNameAsc(StoreStatusType status, StoreSignedStatusType signedStatus, Pageable pageable);
    Page<Store> findAllByStatusAndSignedStatusOrderByStarRatingDesc(StoreStatusType status, StoreSignedStatusType signedStatus,Pageable pageable);
    Page<Store> findAllByStatusAndSignedStatus(StoreStatusType status, StoreSignedStatusType signedStatus, Pageable pageable);
    Page<Store> findAllByNameContainingAndStatusAndSignedStatus(String name, StoreStatusType status, StoreSignedStatusType signedStatus, Pageable pageable);
}


