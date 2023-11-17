package zeobase.zbtechnical.challenges.dto.store;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import zeobase.zbtechnical.challenges.type.StoreStatusType;

import java.time.LocalTime;

/**
 * 이용자(점주)의 매장 등록 관련 DTO 클래스
 */
public class StoreRegistration {

    @Builder
    @Getter
    public static class Request {

        private String name;

        private Double latitude;

        private Double longitude;

        private String explanation;

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

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        private Long storeId;
    }
}
