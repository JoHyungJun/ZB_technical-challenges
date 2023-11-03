package zeobase.ZB_technical.challenges.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zeobase.ZB_technical.challenges.entity.Reservation;
import zeobase.ZB_technical.challenges.type.ReservationAcceptedType;
import zeobase.ZB_technical.challenges.type.ReservationVisitedType;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationInfoDto {

    private Long id;
    private Long memberId;
    private LocalDateTime reservedDate;
    private ReservationAcceptedType acceptedStatus;
    private ReservationVisitedType visitedType;


    public static ReservationInfoDto fromEntity(Reservation reservation) {

        return ReservationInfoDto.builder()
                .id(reservation.getId())
                .memberId(reservation.getMember().getId())
                .reservedDate(reservation.getReservedDateTime())
                .acceptedStatus(reservation.getAcceptedStatus())
                .visitedType(reservation.getVisitedStatus())
                .build();
    }
}
