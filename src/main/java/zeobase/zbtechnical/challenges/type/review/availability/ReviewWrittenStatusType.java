package zeobase.zbtechnical.challenges.type.review.availability;

/**
 * ReservationStillAvailableReviewingRepository 테이블로 리뷰 작성 검증을 진행할 때,
 * 이미 해당 데이터로 리뷰를 작성했는지의 상태 정보 관련 Enum 클래스
 */
public enum ReviewWrittenStatusType {

    WRITTEN,
    NOT_WRITTEN;
}
