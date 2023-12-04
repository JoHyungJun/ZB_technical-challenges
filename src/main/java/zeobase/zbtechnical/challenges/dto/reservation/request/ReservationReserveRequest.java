package zeobase.zbtechnical.challenges.dto.reservation.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 예약 관련 request DTO 클래스
 * 예약할 사람 수는 필수 입력 정보
 * 예약 테이블 수는 필수가 아님
 */
@Builder
@Getter
public class ReservationReserveRequest {

    private Long storeId;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime reservationDateTime;

    @NotNull
    private Integer reservationPersonCount;

    private Integer reservationTableCount;
}
