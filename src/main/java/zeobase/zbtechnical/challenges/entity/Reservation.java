package zeobase.zbtechnical.challenges.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zeobase.zbtechnical.challenges.exception.ReservationException;
import zeobase.zbtechnical.challenges.type.reservation.ReservationAcceptedType;
import zeobase.zbtechnical.challenges.type.reservation.ReservationVisitedType;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static zeobase.zbtechnical.challenges.type.common.ErrorCode.NOT_FOUND_MEMBER_OWNED_RESERVATION;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.NOT_FOUND_STORE_OWNED_RESERVATION;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.NULL_POINT_PRIMARY_KEY;

/**
 * 에약 관련 Entity 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDateTime reservationDateTime;

    @NotNull
    private LocalDate reservationDate;

    @NotNull
    private Integer reservationPersonCount;

    @NotNull
    private Integer reservationTableCount;

    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private ReservationAcceptedType acceptedStatus = ReservationAcceptedType.WAITING;

    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private ReservationVisitedType visitedStatus = ReservationVisitedType.UNVISITED;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;


    public Reservation modifyAccepted(ReservationAcceptedType reservationAcceptedType){

        this.acceptedStatus = reservationAcceptedType;

        return this;
    }

    public Reservation modifyVisited(ReservationVisitedType reservationVisitedType){

        this.visitedStatus = reservationVisitedType;

        return this;
    }

    public Reservation modifyReservationDateTime(LocalDateTime reservationDateTime) {

        this.reservationDateTime = reservationDateTime;
        this.reservationDate = reservationDateTime.toLocalDate();

        return this;
    }

    public Reservation modifyReservationPersonCount(Integer reservationPersonCount) {

        this.reservationPersonCount = reservationPersonCount;

        return this;
    }

    public Reservation modifyReservationTableCount(Integer reservationTableCount) {

        this.reservationTableCount = reservationTableCount;

        return this;
    }

    /**
     * 해당 reservation 의 연관관계의 주인이 되는 member 의 id 를 추출하는 메서드
     * member id 가 null 이라면 예외 처리
     * 내부적으로 getMemberByValidate() 메서드를 통해 검증
     *
     * @return member id
     * @exception ReservationException
     */
    public Long getMemberId() {

        Long validatedId = getMemberByValidate().getId();

        if(validatedId == null) {
            throw new ReservationException(NULL_POINT_PRIMARY_KEY);
        }

        return validatedId;
    }

    /**
     * 해당 reservation 의 연관관계의 주인이 되는 member 을 추출하는 메서드
     * member 가 null 이라면 예외 처리
     *
     * @return "entity/Member"
     * @exception ReservationException
     */
    public Member getMemberByValidate() {

        if(this.member == null) {
            throw new ReservationException(NOT_FOUND_MEMBER_OWNED_RESERVATION);
        }

        return this.member;
    }

    /**
     * 해당 reservation 의 연관관계의 주인이 되는 store 의 id 를 추출하는 메서드
     * store id 가 null 이라면 예외 처리
     * 내부적으로 getStoreByValidate() 메서드를 통해 검증
     *
     * @return store id
     * @exception ReservationException
     */
    public Long getStoreId() {

        Long validatedId = getStoreByValidate().getId();

        if(validatedId == null) {
            throw new ReservationException(NULL_POINT_PRIMARY_KEY);
        }

        return validatedId;
    }

    /**
     * 해당 reservation 의 연관관계의 주인이 되는 store 을 추출하는 메서드
     * Store 가 null 이라면 예외 처리
     *
     * @return "entity/Store"
     * @exception ReservationException
     */
    public Store getStoreByValidate() {

        if(this.store == null) {
            throw new ReservationException(NOT_FOUND_STORE_OWNED_RESERVATION);
        }

        return this.store;
    }
}
