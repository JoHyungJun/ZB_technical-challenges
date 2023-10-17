package zeobase.ZB_technical.challenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zeobase.ZB_technical.challenges.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
