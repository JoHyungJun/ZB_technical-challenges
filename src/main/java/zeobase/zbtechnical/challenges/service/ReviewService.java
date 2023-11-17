package zeobase.zbtechnical.challenges.service;

import org.springframework.security.core.Authentication;
import zeobase.zbtechnical.challenges.dto.review.ReviewInfoResponse;
import zeobase.zbtechnical.challenges.dto.review.ReviewPost;

import java.util.List;

/**
 * 리뷰 관련 Service 의 부모 인터페이스
 */
public interface ReviewService {

    ReviewInfoResponse getReviewById(Long reviewId);
    List<ReviewInfoResponse> getAllReviewsByMemberId(Long memberId);
    List<ReviewInfoResponse> getAllReviewsByStoreId(Long storeId);
    ReviewPost.Response postReview(ReviewPost.Request request, Authentication authentication);
}
