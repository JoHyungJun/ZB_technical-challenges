package zeobase.zbtechnical.challenges.dto.reservation.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 예약 관련 response DTO 클래스
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationReserveResponse {

    private Long reservationId;

    private Long memberId;

    private Long storeId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime reservedDateTime;

    private Integer reservationPersonCount;

    private Integer reservationTableCount;
}
