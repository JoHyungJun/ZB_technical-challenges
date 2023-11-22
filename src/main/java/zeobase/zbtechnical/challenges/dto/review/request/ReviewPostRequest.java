package zeobase.zbtechnical.challenges.dto.review.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 리뷰 등록 관련 request DTO 클래스
 */
@Builder
@Getter
public class ReviewPostRequest {

    private Long storeId;

    @Min(0) @Max(5)
    private Double starRating;

    private String reviewMessage;
}
