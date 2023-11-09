package zeobase.zbtechnical.challenges.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zeobase.zbtechnical.challenges.entity.Review;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 리뷰 등록 관련 DTO 클래스
 */
public class ReviewPostDto {

    @Builder
    @Getter
    public static class Request {

        private Long storeId;

        @Min(0) @Max(5)
        private Double starRating;

        private String reviewMessage;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private Long memberId;
        private Long storeId;
        private Long reviewId;


        public static ReviewPostDto.Response fromEntity(Review review) {

            return Response.builder()
                    .memberId(review.getMember().getId())
                    .storeId(review.getStore().getId())
                    .reviewId(review.getId())
                    .build();
        }
    }
}
