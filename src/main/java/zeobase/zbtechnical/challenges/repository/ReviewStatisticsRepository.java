package zeobase.zbtechnical.challenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import zeobase.zbtechnical.challenges.entity.ReviewStatistics;

import java.util.List;
import java.util.Set;


public interface ReviewStatisticsRepository extends JpaRepository<ReviewStatistics, Long> {

    @Query(
            nativeQuery = true,
            value = "SELECT * " +
                    "FROM review_statistics " +
                        "JOIN (" +
                            "SELECT " +
                                "store_id " +
                                ", MAX(created_at) AS created_at " +
                            "FROM review_statistics " +
                            "WHERE store_id IN (:storeIds) " +
                            "GROUP BY store_id " +
                        ")" +
                        "USING(store_id, created_at) "
    )
    List<ReviewStatistics> findNearestAllByStoreIds(Set<Long> storeIds);
    boolean existsByStoreId(long storeId);
}
