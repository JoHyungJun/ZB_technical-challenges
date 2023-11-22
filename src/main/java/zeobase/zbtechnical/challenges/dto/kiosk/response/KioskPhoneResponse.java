package zeobase.zbtechnical.challenges.dto.kiosk.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 키오스크의 핸드폰 인증 관련 response DTO 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KioskPhoneResponse {

    private Boolean reservationChecked;
}
