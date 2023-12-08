package zeobase.zbtechnical.challenges.dto.review.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static zeobase.zbtechnical.challenges.type.common.ErrorMessage.INVALID_STAR_RATING_MSG;
import static zeobase.zbtechnical.challenges.utils.ValidateConstants.MAX_STAR_RATING;
import static zeobase.zbtechnical.challenges.utils.ValidateConstants.MIN_STAR_RATING;

/**
 * 리뷰 등록 관련 request DTO 클래스
 */
@Builder
@Getter
public class ReviewWriteRequest {

    private Long reservationId;

    private Long storeId;

    @Max(value = MAX_STAR_RATING, message = INVALID_STAR_RATING_MSG)
    @Min(value = MIN_STAR_RATING, message = INVALID_STAR_RATING_MSG)
    private Double starRating;

    private String reviewMessage;
}
