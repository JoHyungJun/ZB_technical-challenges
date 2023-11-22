package zeobase.zbtechnical.challenges.dto.review.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zeobase.zbtechnical.challenges.entity.Review;

/**
 * 공개된 리뷰 정보 관련 response DTO 클래스
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewInfoResponse {

    private Long reviewId;
    private Long memberId;
    private Long storeId;
    private Double starRating;
    private String reviewMessage;


    public static ReviewInfoResponse fromEntity(Review review) {

        return ReviewInfoResponse.builder()
                .reviewId(review.getId())
                .memberId(review.getMember().getId())
                .storeId(review.getStore().getId())
                .starRating(review.getStartRating())
                .reviewMessage(review.getReviewMessage())
                .build();
    }
}
