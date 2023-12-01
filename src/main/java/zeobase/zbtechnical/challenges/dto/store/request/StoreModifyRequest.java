package zeobase.zbtechnical.challenges.dto.store.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zeobase.zbtechnical.challenges.type.store.StoreStatusType;

import java.time.LocalTime;

/**
 * 이용자(점주)의 매장 정보 수정 관련 request DTO 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreModifyRequest {

    private Long storeId;

    private String name;

    private Double latitude;

    private Double longitude;

    private String explanation;

    private StoreStatusType status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime openHours;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime closedHours;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime reservationTerm;

    private Integer tableCount;

    private Integer seatingCapacityPerTable;
}
