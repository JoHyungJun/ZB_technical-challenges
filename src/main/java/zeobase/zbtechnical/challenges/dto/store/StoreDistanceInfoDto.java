package zeobase.zbtechnical.challenges.dto.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.transaction.annotation.Transactional;
import zeobase.zbtechnical.challenges.entity.Store;
import zeobase.zbtechnical.challenges.utils.Distance;

/**
 * 매장 정보와 거리 정보 관련 DTO 클래스
 */
@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreDistanceInfoDto extends StoreInfoDto{

    private Double distanceDiff;


    @Transactional
    public static StoreDistanceInfoDto fromEntity(Store store, Double locationX, Double locationY) {

        Double distanceDiff = null;
        if(locationX != null & locationY != null) {
            distanceDiff = Distance.getDistanceInKilometerByHarversine(
                    locationX, locationY,
                    store.getLatitude(), store.getLongitude());
        }

        return StoreDistanceInfoDto.builder()
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
                .distanceDiff(distanceDiff)
                .build();
    }
}
