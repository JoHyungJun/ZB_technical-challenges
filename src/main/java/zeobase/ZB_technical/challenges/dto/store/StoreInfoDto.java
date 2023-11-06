package zeobase.ZB_technical.challenges.dto.store;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import zeobase.ZB_technical.challenges.entity.Store;
import zeobase.ZB_technical.challenges.type.StoreStatusType;
import zeobase.ZB_technical.challenges.utils.Distance;

import java.awt.*;
import java.time.LocalTime;

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

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
        private LocalTime openHours;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
        private LocalTime closedHours;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
        private LocalTime reservationTerm;

        private Double averageStarRating;

        private Double distanceDiff;


        public static StoreInfoDto.Response fromEntity(Store store) {

            return Response.builder()
                    .id(store.getId())
                    .name(store.getName())
                    .latitude(store.getLatitude())
                    .longitude(store.getLongitude())
                    .explanation(store.getExplanation())
                    .status(store.getStatus())
                    .averageStarRating(store.getAverageStarRating())
                    .openHours(store.getOpenHours())
                    .closedHours(store.getClosedHours())
                    .reservationTerm(store.getReservationTerm())
                    .distanceDiff(null)
                    .build();
        }

        @Transactional
        public static StoreInfoDto.Response fromEntity(Store store, Double locationX, Double locationY) {

            return Response.builder()
                    .id(store.getId())
                    .name(store.getName())
                    .latitude(store.getLatitude())
                    .longitude(store.getLongitude())
                    .explanation(store.getExplanation())
                    .status(store.getStatus())
                    .averageStarRating(store.getAverageStarRating())
                    .distanceDiff(Distance.getDistanceInKilometerByHarversine(
                            locationX, locationY,
                            store.getLatitude(), store.getLongitude()
                    ))
                    .build();
        }
    }
}
