package zeobase.zbtechnical.challenges.type.redis.value;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import zeobase.zbtechnical.challenges.type.common.SQLType;

@Builder
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class CacheReviewStarRating {

    private SQLType sqlType;

    private long reviewId;

    private long storeId;

    private Double starRating;

    @Builder.Default
    private Double previousStarRatingForUpdate = null;
}
