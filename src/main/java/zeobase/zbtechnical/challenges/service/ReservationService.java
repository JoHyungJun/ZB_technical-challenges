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

    // GET
    ReservationInfoResponse getReservationInfoById(Long reservationId);
    List<ReservationInfoResponse> getReservationsInfoByStore(Long storeId);
    List<ReservationInfoResponse> getReservationsInfoByMember(Authentication authentication);
    ReservationAvailableResponse checkAvailableReservationTime(Long storeId, LocalDateTime reservationTime, Integer personCount, Integer tableCount);

    // POST
    ReservationReserveResponse registerReservation(ReservationReserveRequest request, Authentication authentication);
    ReservationAcceptResponse acceptReservationByStoreOwner(ReservationAcceptRequest request, Authentication authentication);

    // UPDATE
    ReservationModifyResponse modifyReservation(Long reservationId, ReservationModifyRequest request, Authentication authentication);

    // DELETE
    ReservationCanceledResponse cancelReservationByMember(Long reservationId, Authentication authentication);
}