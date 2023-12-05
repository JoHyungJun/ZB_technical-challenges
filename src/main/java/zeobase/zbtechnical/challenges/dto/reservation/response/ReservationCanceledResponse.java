package zeobase.zbtechnical.challenges.dto.reservation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCanceledResponse {

    private Long reservationId;
}
