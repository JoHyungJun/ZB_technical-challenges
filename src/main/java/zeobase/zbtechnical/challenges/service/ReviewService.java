package zeobase.zbtechnical.challenges.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import zeobase.zbtechnical.challenges.dto.review.request.ReviewPostRequest;
import zeobase.zbtechnical.challenges.dto.review.response.ReviewHideResponse;
import zeobase.zbtechnical.challenges.dto.review.response.ReviewInfoResponse;
import zeobase.zbtechnical.challenges.dto.review.response.ReviewPostResponse;

/**
 * 리뷰 관련 Service 의 부모 인터페이스
 */
public interface ReviewService {

    // GET
    ReviewInfoResponse getReviewById(Long reviewId);
    Page<ReviewInfoResponse> getAllReviewsByMemberId(Long memberId, Pageable pageable);
    Page<ReviewInfoResponse> getAllReviewsByStoreId(Long storeId, Pageable pageable);

    // POST
    ReviewPostResponse postReview(ReviewPostRequest request, Authentication authentication);

    // UPDATE

    // DELETE
    ReviewHideResponse hideReview(Long reviewId, Authentication authentication);
}
