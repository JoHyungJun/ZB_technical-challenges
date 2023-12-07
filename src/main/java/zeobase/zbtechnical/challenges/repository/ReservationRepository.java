package zeobase.zbtechnical.challenges.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import zeobase.zbtechnical.challenges.entity.Reservation;
import zeobase.zbtechnical.challenges.type.reservation.ReservationVisitedType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 예약 관련 JpaRepository 인터페이스
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByStoreId(Long storeId, Sort sort);
    List<Reservation> findAllReservationByStoreIdAndReservationDate(Long storeId, LocalDate reservationDate);
    Optional<Reservation> findByMemberIdAndStoreId(Long memberId, Long storeId);
    Optional<Reservation> findTopByMemberIdAndStoreIdAndReservationDateTimeAndVisitedStatus(Long storeId, Long memberId, LocalDateTime reservationDateTime, ReservationVisitedType visitedStatus);
}
