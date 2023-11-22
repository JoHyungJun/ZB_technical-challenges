package zeobase.zbtechnical.challenges.dto.review.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zeobase.zbtechnical.challenges.entity.Review;

/**
 * 리뷰 등록 관련 response DTO 클래스
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPostResponse {

    private Long memberId;
    private Long storeId;
    private Long reviewId;


    public static ReviewPostResponse fromEntity(Review review) {

        return ReviewPostResponse.builder()
                .memberId(review.getMember().getId())
                .storeId(review.getStore().getId())
                .reviewId(review.getId())
                .build();
    }
}
