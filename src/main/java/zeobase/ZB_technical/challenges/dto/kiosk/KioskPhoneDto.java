package zeobase.ZB_technical.challenges.dto.kiosk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

public class KioskPhoneDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        @Pattern(regexp = "^01([0|1|6|7|8|9])([0-9]{3,4})([0-9]{4})",
                message = "올바른 양식의 핸드폰 번호가 아닙니다.")
        private String phone;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private String message;
    }
}
