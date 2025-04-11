package zeobase.zbtechnical.challenges.type.redis.key;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * redis 의 일관성을 위해 key 를 관리하는 Enum 클래스
 */
@Getter
@AllArgsConstructor
public enum RedisKeyType {

    REVIEW_STATISTICS_STAR_RATING_UPDATE("reviewStatisticsStarRatingUpdate"),

    ;

    private final String stringKey;
}
