package zeobase.zbtechnical.challenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zeobase.zbtechnical.challenges.entity.ReservationStillAvailableReviewing;
import zeobase.zbtechnical.challenges.type.review.availability.ReviewWrittenStatusType;

import java.time.LocalDate;
import java.util.Optional;

public interface ReservationStillAvailableReviewingRepository extends JpaRepository<ReservationStillAvailableReviewing, Long> {

    void deleteAllByVisitedDateBefore(LocalDate visitedDate);
    Optional<ReservationStillAvailableReviewing> findTopByMemberIdAndStoreIdAndStatus(Long memberId, Long storeId, ReviewWrittenStatusType status);
    Optional<ReservationStillAvailableReviewing> findByReservationId(Long reservationId);
}
