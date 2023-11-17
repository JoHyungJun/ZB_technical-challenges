package zeobase.zbtechnical.challenges.service;

import org.springframework.security.core.Authentication;
import zeobase.zbtechnical.challenges.dto.reservation.ReservationAccept;
import zeobase.zbtechnical.challenges.dto.reservation.ReservationAvailableResponse;
import zeobase.zbtechnical.challenges.dto.reservation.ReservationReserve;
import zeobase.zbtechnical.challenges.dto.reservation.ReservationInfoResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 예약 관련 Service 의 부모 인터페이스
 */
public interface ReservationService {

    ReservationInfoResponse getReservationInfoById(Long reservationId);
    List<ReservationInfoResponse> getReservationsInfoByStoreId(Long storeId);
    ReservationAvailableResponse existsAvailableReservationTime(Long storeId, LocalDateTime reservationTime);
    ReservationReserve.Response reserve(ReservationReserve.Request request, Authentication authentication);
    ReservationAccept.Response acceptReservationByStoreOwner(ReservationAccept.Request request, Authentication authentication);
}
