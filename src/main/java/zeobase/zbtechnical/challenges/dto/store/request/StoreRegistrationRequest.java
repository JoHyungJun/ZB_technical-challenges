package zeobase.zbtechnical.challenges.dto.store.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import zeobase.zbtechnical.challenges.type.store.StoreStatusType;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

/**
 * 이용자(점주)의 매장 등록 관련 request DTO 클래스
 */
@Builder
@Getter
public class StoreRegistrationRequest {

    @NotNull
    private String name;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @NotNull
    private String explanation;

    @NotNull
    private StoreStatusType status;

    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime openHours;

    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime closedHours;

    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime reservationTerm;
}