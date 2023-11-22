package zeobase.zbtechnical.challenges.dto.reservation.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 예약 관련 request DTO 클래스
 */
@Builder
@Getter
public class ReservationReserveRequest {

    private Long storeId;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime reserveDateTime;
}
