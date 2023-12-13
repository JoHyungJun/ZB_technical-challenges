package zeobase.zbtechnical.challenges.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zeobase.zbtechnical.challenges.exception.StoreException;
import zeobase.zbtechnical.challenges.type.store.StoreSignedStatusType;
import zeobase.zbtechnical.challenges.type.store.StoreStatusType;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

import static zeobase.zbtechnical.challenges.type.common.ErrorCode.NOT_FOUND_MEMBER_OWNED_STORE;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.NULL_POINT_PRIMARY_KEY;

/**
 * 매장 관련 Entity 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private String explanation;

    @Enumerated(value = EnumType.STRING)
    private StoreStatusType status;

    @Enumerated(value = EnumType.STRING)
    private StoreSignedStatusType signedStatus;

    private LocalTime openHours;

    private LocalTime closedHours;

    private Double starRating;

    private Long reviewParticipantCount;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "store")
    private List<Review> reviews;

    @OneToMany(mappedBy = "store")
    private List<Reservation> reservations;

    @OneToOne
    @JoinColumn(name = "reservation_info_id")
    private StoreReservationInfo storeReservationInfo;


    public Store modifyName(String name) {

        this.name = name;

        return this;
    }

    public Store modifyPosition(Double latitude, Double longitude) {

        this.latitude = latitude;
        this.longitude = longitude;

        return this;
    }

    public Store modifyExplanation(String explanation) {

        this.explanation = explanation;

        return this;
    }

    public Store modifyStatus(StoreStatusType status) {

        this.status = status;

        return this;
    }

    public Store modifySignedStatus(StoreSignedStatusType signedStatus) {

        this.signedStatus = signedStatus;

        return this;
    }

    public Store modifyOpenHours(LocalTime openHours) {

        this.openHours = openHours;

        return this;
    }

    public Store modifyClosedHours(LocalTime closedHours) {

        this.closedHours = closedHours;

        return this;
    }

    public Double getAverageStarRating() {

        if(this.reviews.size() == 0) {
            return 0.0;
        }

        Double totalRating = this.reviews
                .stream()
                .mapToDouble(review -> review.getStartRating())
                .sum();

        return totalRating / (double) this.reviews.size();
    }

    /**
     * 해당 store 에게 연관관계로 의존하는 storeReservationInfo 의 id 를 추출하는 메서드
     * storeReservationInfo id 가 null 이라면 예외 처리
     * 내부적으로 getStoreReservationInfoByValidate() 메서드를 통해 검증
     *
     * @return storeReservationInfo id
     * @exception StoreException
     */
    public Long getStoreReservationInfoIdByValidate() {

        Long validatedId = getStoreReservationInfoByValidate().getId();

        if(validatedId == null) {
            throw new StoreException(NULL_POINT_PRIMARY_KEY);
        }

        return validatedId;
    }

    /**
     * 해당 store 에게 연관관계로 의존하는 storeReservationInfo 을 추출하는 메서드
     * storeReservationInfo 가 null 이라면 예외 처리
     *
     * @return "entity/StoreReservationInfo"
     * @exception StoreException
     */
    public StoreReservationInfo getStoreReservationInfoByValidate() {

        if(this.storeReservationInfo == null) {
            throw new StoreException(NOT_FOUND_MEMBER_OWNED_STORE);
        }

        return this.storeReservationInfo;
    }

    /**
     * 해당 store 의 연관관계의 주인이 되는 member 의 id 를 추출하는 메서드
     * member id 가 null 이라면 예외 처리
     * 내부적으로 getMemberByValidate() 메서드를 통해 검증
     *
     * @return member id
     * @exception StoreException
     */
    public Long getMemberIdByValidate() {

        Long validatedId = getMemberByValidate().getId();

        if(validatedId == null) {
            throw new StoreException(NULL_POINT_PRIMARY_KEY);
        }

        return validatedId;
    }

    /**
     * 해당 store 의 연관관계의 주인이 되는 member 을 추출하는 메서드
     * member 가 null 이라면 예외 처리
     *
     * @return "entity/Member"
     * @exception StoreException
     */
    public Member getMemberByValidate() {

        if(this.member == null) {
            throw new StoreException(NOT_FOUND_MEMBER_OWNED_STORE);
        }

        return this.member;
    }
}
