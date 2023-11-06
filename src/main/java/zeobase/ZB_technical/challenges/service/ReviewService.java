package zeobase.ZB_technical.challenges.service;

import org.springframework.security.core.Authentication;
import zeobase.ZB_technical.challenges.dto.review.ReviewInfoDto;
import zeobase.ZB_technical.challenges.dto.review.ReviewPostDto;

import java.util.List;
import java.util.Optional;

/**
 * 리뷰 관련 Service 의 부모 인터페이스
 */
public interface ReviewService {

    ReviewInfoDto getReviewById(Long reviewId);
    List<ReviewInfoDto> getAllReviewsByMemberId(Long memberId);
    List<ReviewInfoDto> getAllReviewsByStoreId(Long storeId);
    ReviewPostDto.Response postReview(ReviewPostDto.Request request, Authentication authentication);
}
