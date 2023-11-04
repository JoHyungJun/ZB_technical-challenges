package zeobase.ZB_technical.challenges.service;

import org.springframework.security.core.Authentication;
import zeobase.ZB_technical.challenges.dto.reservation.ReservationAcceptDto;
import zeobase.ZB_technical.challenges.dto.reservation.ReservationAvailableDto;
import zeobase.ZB_technical.challenges.dto.reservation.ReservationReserveDto;
import zeobase.ZB_technical.challenges.dto.reservation.ReservationInfoDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationService {

    List<ReservationInfoDto> getReservationsInfoByStoreId(Long storeId);
    ReservationAvailableDto existsAvailableReservationTime(Long storeId, LocalDateTime reservationTime);
    ReservationReserveDto.Response reserve(ReservationReserveDto.Request request, Authentication authentication);
    ReservationAcceptDto.Response acceptReservationByStoreOwner(ReservationAcceptDto.Request request, Authentication authentication);
}
