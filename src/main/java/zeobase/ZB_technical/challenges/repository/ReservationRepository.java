package zeobase.ZB_technical.challenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zeobase.ZB_technical.challenges.entity.Reservation;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByMemberId(Long memberId);
}
