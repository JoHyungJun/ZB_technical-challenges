package zeobase.ZB_technical.challenges.service;

import zeobase.ZB_technical.challenges.dto.kiosk.KioskPhoneDto;

public interface KioskService {

    KioskPhoneDto.Response checkReservationByPhone(KioskPhoneDto.Request kioskPhoneRequest);
}
