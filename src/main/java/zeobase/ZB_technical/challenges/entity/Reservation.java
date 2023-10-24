package zeobase.ZB_technical.challenges.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import zeobase.ZB_technical.challenges.type.ReservationAcceptedType;
import zeobase.ZB_technical.challenges.type.ReservationVisitedType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Reservation extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@NonNull
    private LocalDateTime reservedDate;

    @Enumerated(value = EnumType.STRING)
    private ReservationAcceptedType accepted;

    @Enumerated
    @Builder.Default
    private ReservationVisitedType visited = ReservationVisitedType.UNVISITED;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;


    public Reservation updateVisited(ReservationVisitedType reservationVisitedType){

        this.visited = reservationVisitedType;

        return this;
    }

}
