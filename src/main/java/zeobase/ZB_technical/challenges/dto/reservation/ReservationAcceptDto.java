package zeobase.ZB_technical.challenges.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zeobase.ZB_technical.challenges.type.ReservationAcceptedType;

public class ReservationAcceptDto {

    @Builder
    @Getter
    public static class Request {

        private Long reservationId;
        private Long storeId;
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
