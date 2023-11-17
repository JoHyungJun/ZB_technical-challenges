package zeobase.zbtechnical.challenges.service;

import zeobase.zbtechnical.challenges.dto.kiosk.KioskPhone;
import zeobase.zbtechnical.challenges.dto.kiosk.KioskSigninRequest;

/**
 * 키오스크 관련 Service 의 부모 인터페이스
 */
public interface KioskService {

    KioskPhone.Response checkReservationByPhone(KioskPhone.Request request);
    KioskSigninRequest.Response checkReservationByMember(KioskSigninRequest.Request request);
}
