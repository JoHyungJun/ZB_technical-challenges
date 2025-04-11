package zeobase.zbtechnical.challenges.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import zeobase.zbtechnical.challenges.entity.ReviewStatistics;
import zeobase.zbtechnical.challenges.repository.ReviewStatisticsRepository;
import zeobase.zbtechnical.challenges.type.redis.value.CacheReviewStarRating;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static zeobase.zbtechnical.challenges.type.common.SQLType.INSERT;
import static zeobase.zbtechnical.challenges.type.redis.key.RedisKeyType.REVIEW_STATISTICS_STAR_RATING_UPDATE;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewScheduler {

    private final RedisTemplate<String, Object> redisTemplate;

    private final ReviewStatisticsRepository reviewStatisticsRepository;


    @Transactional
    @Scheduled(cron = "${spring.scheduler.reviewStatistics.cron}")
    public void cachingReviewFromRedis() {

        List<CacheReviewStarRating> cachedReviewsStarRatings = redisTemplate.opsForSet()
                .members(REVIEW_STATISTICS_STAR_RATING_UPDATE.getStringKey())
                .stream()
                .map(object -> (CacheReviewStarRating) object)
                .sorted(Comparator.comparingInt(cacheReviewStarRating -> cacheReviewStarRating.getSqlType() == INSERT ? 0 : 1))
                .collect(Collectors.toList());

        Set<Long> targetStoreIds = cachedReviewsStarRatings.stream()
                .mapToLong(CacheReviewStarRating::getStoreId)
                .boxed()
                .collect(Collectors.toSet());

        // 등록/수정/삭제에 해당하는 리뷰들의 가게는 무조건 이벤트 리스너에 의해 reviewStatisticsRepository 에 있음
        Map<Long, ReviewStatistics> cachedReviewStatisticsMap = reviewStatisticsRepository.findNearestAllByStoreIds(targetStoreIds)
                .stream()
                .collect(Collectors.toMap(
                        ReviewStatistics::getStoreId,
                        Function.identity()));


        ReviewStatistics reviewStatistics;
        for(CacheReviewStarRating cachedReviewStarRating : cachedReviewsStarRatings) {

            reviewStatistics = cachedReviewStatisticsMap.get(cachedReviewStarRating.getStoreId());
            if(reviewStatistics == null) {
                log.error("storeId does not exist in ReviewStatisticsRepository -> {}", cachedReviewStarRating.getStoreId());
                continue;
            }

            switch (cachedReviewStarRating.getSqlType()) {
                case INSERT:
                    reviewStatistics.increaseTotalStarRating(cachedReviewStarRating.getStarRating());
                    break;

                case UPDATE:
                    reviewStatistics.modifyTotalStarRating(cachedReviewStarRating.getPreviousStarRatingForUpdate(), cachedReviewStarRating.getStarRating());
                    break;

                case DELETE:
                    reviewStatistics.decreaseTotalStarRating(cachedReviewStarRating.getStarRating());
                    break;
            }
        }

        redisTemplate.delete(REVIEW_STATISTICS_STAR_RATING_UPDATE.getStringKey());
    }
}
