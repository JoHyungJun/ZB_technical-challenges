package zeobase.zbtechnical.challenges.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeobase.zbtechnical.challenges.repository.ReservationStillAvailableReviewingRepository;

import java.time.LocalDate;

/**
 * 리뷰 작성 검증용 엔티티의 스케줄링 관련 로직을 담는 Service 클래스
 */
@Service
@RequiredArgsConstructor
public class ReservationStillAvailableReviewingScheduler {

    // 최대 리뷰를 남길 수 있는 기간은 방문 날 다음 날부터 최대 7일 (다음 주 같은 요일까지 작성 가능)
    private final Long MAX_REVIEW_AVAILABILITY_DAYS = 7l;

    private final ReservationStillAvailableReviewingRepository reservationStillAvailableReviewingRepository;


    /**
     * ReservationStillAvailableReviewingRepository 의 데이터를 일주일 간격으로 삭제하는 스케줄러 메서드
     * (다음 주 같은 요일의 다음 날 00:00 에 데이터가 삭제됨)
     * 해당 테이블에 데이터가 없다면 고객은 리뷰 작성이 불가함
     * 방문한 고객은 다음 주, 방문 당일 같은 요일까지 리뷰를 쓸 수 있음
     *
     * @param
     * @return
     */
    @Transactional
    @Scheduled(cron = "${spring.scheduler.reviewAvailabilityVisitedReservation.cron}")
    public void deleteReviewAvailabilityVisitedReservationAfterVisited7Days() {

        LocalDate expiredDate = LocalDate.now().minusDays(MAX_REVIEW_AVAILABILITY_DAYS);

        reservationStillAvailableReviewingRepository.deleteAllByVisitedDateBefore(expiredDate);
    }
}
