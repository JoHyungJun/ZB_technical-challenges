package zeobase.zbtechnical.challenges.service;

import org.springframework.security.core.Authentication;
import zeobase.zbtechnical.challenges.dto.reservation.response.*;
import zeobase.zbtechnical.challenges.dto.reservation.request.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 예약 관련 Service 의 부모 인터페이스
 */
public interface ReservationService {

    ReservationInfoResponse getReservationInfoById(Long reservationId);
    List<ReservationInfoResponse> getReservationsInfoByStoreId(Long storeId);
    ReservationAvailableResponse existsAvailableReservationTime(Long storeId, LocalDateTime reservationTime);
    ReservationReserveResponse reserve(ReservationReserveRequest request, Authentication authentication);
    ReservationAcceptResponse acceptReservationByStoreOwner(ReservationAcceptRequest request, Authentication authentication);
}
