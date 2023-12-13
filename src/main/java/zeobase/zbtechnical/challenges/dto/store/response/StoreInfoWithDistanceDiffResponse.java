package zeobase.zbtechnical.challenges.dto.store.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.transaction.annotation.Transactional;
import zeobase.zbtechnical.challenges.dto.store.StoreInfoWithDistanceDiffDto;
import zeobase.zbtechnical.challenges.entity.Store;
import zeobase.zbtechnical.challenges.utils.DistanceUtils;

/**
 * 매장 정보와 거리 정보 관련 response DTO 클래스
 */
@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreInfoWithDistanceDiffResponse extends StoreInfoResponse {

    private Double distanceDiff;


    @Transactional
    public static StoreInfoWithDistanceDiffResponse fromEntity(Store store, Double locationX, Double locationY) {

        Double distanceDiff = null;
        if(locationX != null & locationY != null) {
            distanceDiff = DistanceUtils.getDistanceInKilometerByHarversine(
                    locationX, locationY,
                    store.getLatitude(), store.getLongitude());
        }

        return StoreInfoWithDistanceDiffResponse.builder()
                .storeId(store.getId())
                .name(store.getName())
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .explanation(store.getExplanation())
                .status(store.getStatus())
                .starRating(store.getAverageStarRating())
                .reviewParticipantCount(store.getReviewParticipantCount())
                .openHours(store.getOpenHours())
                .closedHours(store.getClosedHours())
                .distanceDiff(distanceDiff)
                .build();
    }

    /**
     * StoreInfoWithDistanceDiffDto 인터페이스를
     * StoreInfoWithDistanceDiffResponse 객체로 변환하는 메서드
     *
     * @param dto - StoreInfoWithDistanceDiffDto
     * @return
     */
    public static StoreInfoWithDistanceDiffResponse fromDto(StoreInfoWithDistanceDiffDto dto) {

        return StoreInfoWithDistanceDiffResponse.builder()
                .storeId(dto.getStoreId())
                .name(dto.getName())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .explanation(dto.getExplanation())
                .status(dto.getStatus())
                .openHours(dto.getOpenHours())
                .closedHours(dto.getClosedHours())
                .starRating(dto.getStarRating())
                .reviewParticipantCount(dto.getReviewParticipantCount())
                .distanceDiff(dto.getDistanceDiff())
                .build();
    }
}
