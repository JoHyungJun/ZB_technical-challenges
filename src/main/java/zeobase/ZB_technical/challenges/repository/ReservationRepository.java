package zeobase.ZB_technical.challenges.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zeobase.ZB_technical.challenges.entity.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 예약 관련 JpaRepository 인터페이스
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r " +
           "FROM Reservation AS r " +
           "WHERE r.member.id = :memberId " +
             "AND r.store.id = :storeId " +
             "AND r.reservedDate = :now")
    List<Reservation> findTodayReservationsByMemberIdAndStoreId(@Param("memberId") Long memberId,
                                                                @Param("storeId") Long storeId,
                                                                @Param("now") LocalDate now);
    List<Reservation> findAllByStoreId(Long storeId, Sort sort);
    List<Reservation> findAllReservationByStoreIdAndReservedDate(Long storeId, LocalDate date);
    Optional<Reservation> findByMemberIdAndStoreId(Long memberId, Long storeId);
    Optional<Reservation> findByMemberIdAndStoreIdAndReservedDateTime(Long storeId, Long memberId, LocalDateTime reservedDateTime);
}
