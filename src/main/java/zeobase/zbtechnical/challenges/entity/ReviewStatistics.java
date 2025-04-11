package zeobase.zbtechnical.challenges.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "review_statistics",
        indexes = @Index(name = "idx_review_statistics_created_at", columnList = "store_id, created_at"))
public class ReviewStatistics extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "store_id")
    private Long storeId;

    @NotNull
    private Double starRating;

    @NotNull
    private Double totalStarRating;

    @NotNull
    private Long reviewCount;


    public ReviewStatistics increaseTotalStarRating(Double starRating) {

        this.totalStarRating += starRating;
        this.reviewCount++;
        this.starRating = this.totalStarRating / this.reviewCount;

        return this;
    }

    public ReviewStatistics decreaseTotalStarRating(Double starRating) {

        this.totalStarRating -= starRating;
        this.reviewCount--;

        if(reviewCount == 0) {
            this.starRating =  0.0;
        } else {
            this.starRating = this.totalStarRating / this.reviewCount;
        }

        return this;
    }

    public ReviewStatistics modifyTotalStarRating(double previousStarRating, double currentStarRating) {

        this.totalStarRating += (currentStarRating - previousStarRating);
        this.starRating = this.totalStarRating / this.reviewCount;

        return this;
    }

    public static ReviewStatistics initializeForNewStore(long storeId) {

        return ReviewStatistics.builder()
                .storeId(storeId)
                .starRating(0.0)
                .totalStarRating(0.0)
                .reviewCount(0l)
                .build();
    }
}
