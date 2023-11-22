package zeobase.zbtechnical.challenges.service;

import zeobase.zbtechnical.challenges.dto.kiosk.request.*;
import zeobase.zbtechnical.challenges.dto.kiosk.response.*;

/**
 * 키오스크 관련 Service 의 부모 인터페이스
 */
public interface KioskService {

    KioskPhoneResponse checkReservationByPhone(KioskPhoneRequest request);
    KioskSigninResponse checkReservationByMember(KioskSigninRequest request);
}
