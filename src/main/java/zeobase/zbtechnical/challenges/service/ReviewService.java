package zeobase.zbtechnical.challenges.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import zeobase.zbtechnical.challenges.dto.review.request.ReviewModifyRequest;
import zeobase.zbtechnical.challenges.dto.review.request.ReviewWriteRequest;
import zeobase.zbtechnical.challenges.dto.review.response.ReviewHideResponse;
import zeobase.zbtechnical.challenges.dto.review.response.ReviewInfoResponse;
import zeobase.zbtechnical.challenges.dto.review.response.ReviewModifyResponse;
import zeobase.zbtechnical.challenges.dto.review.response.ReviewWriteResponse;

/**
 * 리뷰 관련 Service 의 부모 인터페이스
 */
public interface ReviewService {

    // GET
    ReviewInfoResponse getReviewInfoById(Long reviewId);
    Page<ReviewInfoResponse> getReviewsInfoByMember(Long memberId, Pageable pageable);
    Page<ReviewInfoResponse> getReviewsInfoByStore(Long storeId, Pageable pageable);

    // POST
    ReviewWriteResponse writeReview(ReviewWriteRequest request, Authentication authentication);

    // UPDATE
    ReviewModifyResponse modifyReview(Long reviewId, ReviewModifyRequest request, Authentication authentication);

    // DELETE
    ReviewHideResponse hideReviewByStoreOwner(Long reviewId, Authentication authentication);
}
