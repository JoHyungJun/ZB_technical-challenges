package zeobase.zbtechnical.challenges.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zeobase.zbtechnical.challenges.type.ReservationAcceptedType;

/**
 * 점주의 예약 승인/허용 관련 DTO 클래스
 */
public class ReservationAccept {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        private Long reservationId;
        private ReservationAcceptedType accepted;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private Long reservationId;
        private ReservationAcceptedType accepted;
    }
}
