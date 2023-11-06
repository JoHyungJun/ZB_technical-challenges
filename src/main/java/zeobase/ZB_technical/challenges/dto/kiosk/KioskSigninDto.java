package zeobase.ZB_technical.challenges.dto.kiosk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 키오스크의 id, password 인증 관련 DTO 클래스
 */
public class KioskSigninDto {

    @Builder
    @Getter
    public static class Request {

        private String memberId;

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
