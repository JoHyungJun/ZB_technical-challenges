package zeobase.zbtechnical.challenges.dto.store.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import zeobase.zbtechnical.challenges.entity.Store;
import zeobase.zbtechnical.challenges.type.StoreStatusType;

import java.time.LocalTime;

/**
 * 매장 정보 관련 DTO 클래스
 */
@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreInfoResponse {

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

    private Double averageStarRating;


    public static StoreInfoResponse fromEntity(Store store) {

        return StoreInfoResponse.builder()
                .storeId(store.getId())
                .name(store.getName())
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .explanation(store.getExplanation())
                .status(store.getStatus())
                .averageStarRating(store.getAverageStarRating())
                .openHours(store.getOpenHours())
                .closedHours(store.getClosedHours())
                .reservationTerm(store.getReservationTerm())
                .build();
    }
}
