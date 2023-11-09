package zeobase.zbtechnical.challenges.entity;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zeobase.zbtechnical.challenges.type.ReservationAcceptedType;
import zeobase.zbtechnical.challenges.type.ReservationVisitedType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 에약 관련 Entity 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Reservation extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDateTime reservedDateTime;

    @NotNull
    private LocalDate reservedDate;

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


    public Reservation updateAccepted(ReservationAcceptedType reservationAcceptedType){

        this.acceptedStatus = reservationAcceptedType;

        return this;
    }

    public Reservation updateVisited(ReservationVisitedType reservationVisitedType){

        this.visitedStatus = reservationVisitedType;

        return this;
    }

}
