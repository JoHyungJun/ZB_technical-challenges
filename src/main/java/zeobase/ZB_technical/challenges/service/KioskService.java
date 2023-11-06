package zeobase.ZB_technical.challenges.service;

import zeobase.ZB_technical.challenges.dto.kiosk.KioskPhoneDto;
import zeobase.ZB_technical.challenges.dto.kiosk.KioskSigninDto;
import zeobase.ZB_technical.challenges.entity.Reservation;

import java.util.List;

/**
 * 키오스크 관련 Service 의 부모 인터페이스
 */
public interface KioskService {

    KioskPhoneDto.Response checkReservationByPhone(KioskPhoneDto.Request request);
    KioskSigninDto.Response checkReservationByMember(KioskSigninDto.Request request);
}
