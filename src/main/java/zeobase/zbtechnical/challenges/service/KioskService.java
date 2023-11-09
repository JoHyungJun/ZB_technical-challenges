package zeobase.zbtechnical.challenges.service;

import zeobase.zbtechnical.challenges.dto.kiosk.KioskPhoneDto;
import zeobase.zbtechnical.challenges.dto.kiosk.KioskSigninDto;

/**
 * 키오스크 관련 Service 의 부모 인터페이스
 */
public interface KioskService {

    KioskPhoneDto.Response checkReservationByPhone(KioskPhoneDto.Request request);
    KioskSigninDto.Response checkReservationByMember(KioskSigninDto.Request request);
}
