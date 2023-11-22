package zeobase.zbtechnical.challenges.dto.reservation.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zeobase.zbtechnical.challenges.type.ReservationAcceptedType;

/**
 * 점주의 예약 승인/허용 관련 request DTO 클래스
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationAcceptRequest {

    private Long reservationId;
    private ReservationAcceptedType accepted;
}
