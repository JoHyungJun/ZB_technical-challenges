package zeobase.ZB_technical.challenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zeobase.ZB_technical.challenges.entity.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);
    List<Review> findAllByStoreIdOrderByCreatedAtDesc(Long storeId);
}
