package zeobase.zbtechnical.challenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zeobase.zbtechnical.challenges.entity.ReviewAvailabilityVisitedReservation;

import java.time.LocalDate;

public interface ReviewAvailabilityVisitedReservationRepository extends JpaRepository<ReviewAvailabilityVisitedReservation, Long> {

    void deleteAllByVisitedDateBefore(LocalDate visitedDate);
}
