package zeobase.zbtechnical.challenges.dto.review.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리뷰 수정 관련 request DTO 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewModifyRequest {

    private Double starRating;

    private String reviewMessage;
}
