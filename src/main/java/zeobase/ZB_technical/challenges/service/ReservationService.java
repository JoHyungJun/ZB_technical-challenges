package zeobase.ZB_technical.challenges.service;

import org.springframework.security.core.Authentication;
import zeobase.ZB_technical.challenges.dto.reservation.ReservationAvailableDto;
import zeobase.ZB_technical.challenges.dto.reservation.ReservationDto;
import zeobase.ZB_technical.challenges.dto.reservation.ReservationInfoDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationService {

    List<ReservationInfoDto> getReservationsInfoByStoreId(Long storeId);
    ReservationAvailableDto existsAvailableReservationTime(Long storeId, LocalDateTime reservationTime);
    ReservationDto.Response reserve(ReservationDto.Request request, Authentication authentication);
}
