package zeobase.zbtechnical.challenges.service;

import org.springframework.security.core.Authentication;
import zeobase.zbtechnical.challenges.dto.review.response.*;
import zeobase.zbtechnical.challenges.dto.review.request.*;

import java.util.List;

/**
 * 리뷰 관련 Service 의 부모 인터페이스
 */
public interface ReviewService {

    ReviewInfoResponse getReviewById(Long reviewId);
    List<ReviewInfoResponse> getAllReviewsByMemberId(Long memberId);
    List<ReviewInfoResponse> getAllReviewsByStoreId(Long storeId);
    ReviewPostResponse postReview(ReviewPostRequest request, Authentication authentication);
}
