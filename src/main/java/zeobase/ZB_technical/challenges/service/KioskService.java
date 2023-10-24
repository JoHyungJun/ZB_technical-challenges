package zeobase.ZB_technical.challenges.service;

import zeobase.ZB_technical.challenges.dto.KioskPhone;

public interface KioskService {

    KioskPhone.Response checkReservationByPhone(KioskPhone.Request kioskPhoneRequest);
}
