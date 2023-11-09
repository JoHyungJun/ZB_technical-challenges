package zeobase.zbtechnical.challenges.service;

import org.springframework.security.core.Authentication;
import zeobase.zbtechnical.challenges.dto.reservation.ReservationAcceptDto;
import zeobase.zbtechnical.challenges.dto.reservation.ReservationAvailableDto;
import zeobase.zbtechnical.challenges.dto.reservation.ReservationReserveDto;
import zeobase.zbtechnical.challenges.dto.reservation.ReservationInfoDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 예약 관련 Service 의 부모 인터페이스
 */
public interface ReservationService {

    ReservationInfoDto getReservationInfoById(Long reservationId);
    List<ReservationInfoDto> getReservationsInfoByStoreId(Long storeId);
    ReservationAvailableDto existsAvailableReservationTime(Long storeId, LocalDateTime reservationTime);
    ReservationReserveDto.Response reserve(ReservationReserveDto.Request request, Authentication authentication);
    ReservationAcceptDto.Response acceptReservationByStoreOwner(ReservationAcceptDto.Request request, Authentication authentication);
}
