package zeobase.zbtechnical.challenges.dto.kiosk.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

/**
 * 키오스크의 id, password 인증 관련 request DTO 클래스
 */
@Builder
@Getter
public class KioskSigninRequest {

    private String UID;

    private String password;

    private Long storeId;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime reservedTime;
}
