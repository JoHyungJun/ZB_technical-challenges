package zeobase.zbtechnical.challenges.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 예약 가능 여부 관련 DTO 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationAvailableResponse {

    private boolean isAvailable;
}
