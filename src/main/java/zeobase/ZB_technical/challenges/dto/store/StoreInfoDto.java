package zeobase.ZB_technical.challenges.dto.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zeobase.ZB_technical.challenges.entity.Store;
import zeobase.ZB_technical.challenges.type.StoreStatusType;
import zeobase.ZB_technical.challenges.utils.Distance;

import java.awt.*;

public class StoreInfoDto {

    @Builder
    @Getter
    public static class Request {

        private Double latitude;
        private Double longitude;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        private Long id;
        private String name;
        private Double latitude;
        private Double longitude;
        private String explanation;
        private StoreStatusType status;
        private Long totalStarRating;
        private Double distanceDiff;


        public static StoreInfoDto.Response fromEntity(Store store) {

            return Response.builder()
                    .id(store.getId())
                    .name(store.getName())
                    .latitude(store.getLatitude())
                    .longitude(store.getLongitude())
                    .explanation(store.getExplanation())
                    .status(store.getStatus())
                    .totalStarRating(store.getTotalStarRating())
                    .distanceDiff(null)
                    .build();
        }

        public static StoreInfoDto.Response fromEntity(Store store, Double locationX, Double locationY) {

            return Response.builder()
                    .id(store.getId())
                    .name(store.getName())
                    .latitude(store.getLatitude())
                    .longitude(store.getLongitude())
                    .explanation(store.getExplanation())
                    .status(store.getStatus())
                    .totalStarRating(store.getTotalStarRating())
                    .distanceDiff(Distance.getDistanceInKilometerByHarversine(
                            locationX, locationY,
                            store.getLatitude(), store.getLongitude()
                    ))
                    .build();
        }
    }
}
