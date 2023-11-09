package zeobase.zbtechnical.challenges.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zeobase.zbtechnical.challenges.entity.Reservation;
import zeobase.zbtechnical.challenges.type.ReservationAcceptedType;
import zeobase.zbtechnical.challenges.type.ReservationVisitedType;

import java.time.LocalDateTime;

/**
 * 공개된 예약의 정보 관련 DTO 클래스
 */
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
