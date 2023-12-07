package zeobase.zbtechnical.challenges.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zeobase.zbtechnical.challenges.type.review.ReviewStatusType;

import javax.persistence.*;

/**
 * 리뷰 관련 Entity 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double startRating;

    private String reviewMessage;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReviewStatusType status = ReviewStatusType.SHOW;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;


    public Review modifyStatus(ReviewStatusType status) {

        this.status = status;

        return this;
    }

    public Review modifyStarRating(Double startRating) {

        this.startRating = startRating;

        return this;
    }

    public Review modifyReviewMessage(String reviewMessage) {

        this.reviewMessage = reviewMessage;

        return this;
    }
}
