package zeobase.zbtechnical.challenges.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zeobase.zbtechnical.challenges.exception.ReviewException;
import zeobase.zbtechnical.challenges.type.review.ReviewStatusType;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import static zeobase.zbtechnical.challenges.type.common.ErrorCode.NOT_FOUND_MEMBER_OWNED_REVIEW;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.NOT_FOUND_STORE_OWNED_REVIEW;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.NULL_POINT_PRIMARY_KEY;

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

    /**
     * 해당 review 의 연관관계의 주인이 되는 member 의 id 를 추출하는 메서드
     * member id 가 null 이라면 예외 처리
     * 내부적으로 getMemberByValidate() 메서드를 통해 검증
     *
     * @return member id
     * @exception ReviewException
     */
    public Long getMemberId() {

        Long validatedId = getMemberByValidate().getId();

        if(validatedId == null) {
            throw new ReviewException(NULL_POINT_PRIMARY_KEY);
        }

        return validatedId;
    }

    /**
     * 해당 review 의 연관관계의 주인이 되는 member 을 추출하는 메서드
     * member 가 null 이라면 예외 처리
     *
     * @return "entity/Member"
     * @exception ReviewException
     */
    public Member getMemberByValidate() {

        if(this.member == null) {
            throw new ReviewException(NOT_FOUND_MEMBER_OWNED_REVIEW);
        }

        return this.member;
    }

    /**
     * 해당 review 의 연관관계의 주인이 되는 store 의 id 를 추출하는 메서드
     * store id 가 null 이라면 예외 처리
     * 내부적으로 getStoreByValidate() 메서드를 통해 검증
     *
     * @return store id
     * @exception ReviewException
     */
    public Long getStoreId() {

        Long validatedId = getStoreByValidate().getId();

        if(validatedId == null) {
            throw new ReviewException(NULL_POINT_PRIMARY_KEY);
        }

        return validatedId;
    }

    /**
     * 해당 review 의 연관관계의 주인이 되는 store 을 추출하는 메서드
     * store 가 null 이라면 예외 처리
     *
     * @return "entity/Store"
     * @exception ReviewException
     */
    public Store getStoreByValidate() {

        if(this.store == null) {
            throw new ReviewException(NOT_FOUND_STORE_OWNED_REVIEW);
        }

        return this.store;
    }
}
