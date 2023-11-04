package zeobase.ZB_technical.challenges.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import zeobase.ZB_technical.challenges.entity.Reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByMemberId(Long memberId);
    List<Reservation> findAllByStoreId(Long storeId, Sort sort);
    List<Reservation> findAllReservationByStoreIdAndReservedDate(Long storeId, LocalDate date);
    Optional<Reservation> findByMemberIdAndStoreId(Long memberId, Long storeId);
}
