package zeobase.zbtechnical.challenges.dto.kiosk.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Pattern;
import java.time.LocalTime;

/**
 * 키오스크의 핸드폰 인증 관련 request DTO 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KioskPhoneRequest {

    @Pattern(regexp = "^01([0|1|6|7|8|9])([0-9]{3,4})([0-9]{4})",
            message = "올바른 양식의 핸드폰 번호가 아닙니다.")
    private String phone;

    private Long storeId;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime reservedTime;
}