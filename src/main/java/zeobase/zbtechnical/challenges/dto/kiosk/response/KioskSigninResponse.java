package zeobase.zbtechnical.challenges.dto.kiosk.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 키오스크의 id, password 인증 관련 response DTO 클래스
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KioskSigninResponse {

    private Boolean reservationChecked;
}
