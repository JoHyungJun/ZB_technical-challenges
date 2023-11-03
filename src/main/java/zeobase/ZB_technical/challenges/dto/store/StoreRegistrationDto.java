package zeobase.ZB_technical.challenges.dto.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zeobase.ZB_technical.challenges.type.StoreStatusType;

public class StoreRegistrationDto {

    @Builder
    @Getter
    public static class Request {

        private String name;
        private Double latitude;
        private Double longitude;
        private String explanation;
        private StoreStatusType status;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        private Long storeId;
    }
}
