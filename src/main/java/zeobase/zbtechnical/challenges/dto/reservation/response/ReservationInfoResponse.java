package zeobase.zbtechnical.challenges.dto.reservation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zeobase.zbtechnical.challenges.entity.Reservation;
import zeobase.zbtechnical.challenges.type.reservation.ReservationAcceptedType;
import zeobase.zbtechnical.challenges.type.reservation.ReservationVisitedType;

import java.time.LocalDateTime;

/**
 * 공개된 예약의 정보 관련 response DTO 클래스
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationInfoResponse {

    private Long reservationId;
    private Long storeId;
    private Long memberId;
    private LocalDateTime reservationDateTime;
    private Integer reservationPersonCount;
    private ReservationAcceptedType acceptedStatus;
    private ReservationVisitedType visitedType;


    public static ReservationInfoResponse fromEntity(Reservation reservation) {

        return ReservationInfoResponse.builder()
                .reservationId(reservation.getId())
                .storeId(reservation.getStore().getId())
                .memberId(reservation.getMember().getId())
                .reservationDateTime(reservation.getReservationDateTime())
                .reservationPersonCount(reservation.getReservationPersonCount())
                .acceptedStatus(reservation.getAcceptedStatus())
                .visitedType(reservation.getVisitedStatus())
                .build();
    }
}
