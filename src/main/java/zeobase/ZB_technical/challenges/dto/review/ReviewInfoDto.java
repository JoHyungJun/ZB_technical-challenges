package zeobase.ZB_technical.challenges.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zeobase.ZB_technical.challenges.entity.Review;

/**
 * 리뷰 정보 관련 DTO 클래스
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewInfoDto {

    private Long reviewId;
    private Long memberId;
    private Long storeId;
    private Double starRating;
    private String reviewMessage;


    public static ReviewInfoDto fromEntity(Review review) {

        return ReviewInfoDto.builder()
                .reviewId(review.getId())
                .memberId(review.getMember().getId())
                .storeId(review.getStore().getId())
                .starRating(review.getStartRating())
                .reviewMessage(review.getReviewMessage())
                .build();
    }
}
