package zeobase.ZB_technical.challenges.entity;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zeobase.ZB_technical.challenges.type.ReservationAcceptedType;
import zeobase.ZB_technical.challenges.type.ReservationVisitedType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
