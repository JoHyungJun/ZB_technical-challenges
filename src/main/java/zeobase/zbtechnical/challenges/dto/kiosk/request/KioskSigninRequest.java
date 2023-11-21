package zeobase.zbtechnical.challenges.dto.kiosk.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

/**
 * 키오스크의 id, password 인증 관련 request DTO 클래스
 */
public class KioskSigninRequest {

    @Builder
    @Getter
    public static class Request {

        private String UID;

        private String password;

        private Long storeId;

        @DateTimeFormat(pattern = "HH:mm")
        private LocalTime reservedTime;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private Boolean reservationChecked;
    }
}
