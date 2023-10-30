package zeobase.ZB_technical.challenges.dto.kiosk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

public class KioskPhoneDto {

    @Getter
    @Builder
    public static class Request {

        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}",
                message = "올바른 양식의 핸드폰 번호가 아닙니다.")
        private String phone;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private Boolean success;
        private String message;
    }
}
