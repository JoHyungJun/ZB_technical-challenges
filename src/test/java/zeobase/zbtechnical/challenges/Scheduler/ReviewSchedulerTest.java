package zeobase.zbtechnical.challenges.Scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import zeobase.zbtechnical.challenges.entity.ReviewStatistics;
import zeobase.zbtechnical.challenges.entity.Store;
import zeobase.zbtechnical.challenges.repository.ReviewStatisticsRepository;
import zeobase.zbtechnical.challenges.repository.StoreRepository;
import zeobase.zbtechnical.challenges.scheduler.ReviewScheduler;
import zeobase.zbtechnical.challenges.type.redis.value.CacheReviewStarRating;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static zeobase.zbtechnical.challenges.type.common.SQLType.INSERT;
import static zeobase.zbtechnical.challenges.type.common.SQLType.UPDATE;
import static zeobase.zbtechnical.challenges.type.redis.key.RedisKeyType.REVIEW_STATISTICS_STAR_RATING_UPDATE;

@ExtendWith(MockitoExtension.class)
class ReviewSchedulerTest {

    @InjectMocks
    private ReviewScheduler reviewScheduler;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private ReviewStatisticsRepository reviewStatisticsRepository;

    @Mock
    private SetOperations<String, Object> setOperations;

    @Test
    void testCachingReviewFromRedis_success() {

        // given
        Long storeId = 1L;

        CacheReviewStarRating insertCachedReview = CacheReviewStarRating.builder()
                .sqlType(INSERT)
                .reviewId(100L)
                .storeId(storeId)
                .starRating(4.5)
                .build();

        CacheReviewStarRating updateCachedReview = CacheReviewStarRating.builder()
                .sqlType(UPDATE)
                .reviewId(100L)
                .storeId(storeId)
                .starRating(3.5)
                .previousStarRatingForUpdate(4.5)
                .build();


        Set<Object> mockRedisSet = new HashSet<>();
        mockRedisSet.add(insertCachedReview);
        mockRedisSet.add(updateCachedReview);


        ReviewStatistics statistics = ReviewStatistics.initializeForNewStore(storeId);
        Store store = Store.builder()
                .id(storeId)
                .name("Mock Store")
                .latitude(0.0)
                .longitude(0.0)
                .starRating(0.0)
                .build();


        // mock Redis set 연산
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.members(REVIEW_STATISTICS_STAR_RATING_UPDATE.getStringKey()))
                .thenReturn(mockRedisSet);


        // mock 통계, 가게 조회
        when(reviewStatisticsRepository.findNearestAllByStoreIds(Set.of(storeId)))
                .thenReturn(List.of(statistics));

        when(storeRepository.findAllById(Set.of(storeId)))
                .thenReturn(List.of(store));

        // when
        reviewScheduler.cachingReviewFromRedis();

        // then
        assertEquals(3.5, store.getStarRating());
        assertEquals(1, statistics.getReviewCount());
        assertEquals(3.5, statistics.getTotalStarRating());

        // verify
        verify(redisTemplate).delete(REVIEW_STATISTICS_STAR_RATING_UPDATE.getStringKey());
    }
}