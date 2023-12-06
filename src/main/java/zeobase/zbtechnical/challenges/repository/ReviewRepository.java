package zeobase.zbtechnical.challenges.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import zeobase.zbtechnical.challenges.entity.Review;
import zeobase.zbtechnical.challenges.type.review.ReviewStatusType;

/**
 * 리뷰 관련 JpaRepository 인터페이스
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findAllByMemberIdAndStatusOrderByCreatedAtDesc(Long memberId, ReviewStatusType status, Pageable pageable);
    Page<Review> findAllByStoreIdAndStatusOrderByCreatedAtDesc(Long storeId, ReviewStatusType status, Pageable pageable);
}
