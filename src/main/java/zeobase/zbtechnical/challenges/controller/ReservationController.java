package zeobase.zbtechnical.challenges.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import zeobase.zbtechnical.challenges.dto.reservation.response.*;
import zeobase.zbtechnical.challenges.dto.reservation.request.*;
import zeobase.zbtechnical.challenges.exception.StoreException;
import zeobase.zbtechnical.challenges.service.ReservationService;
import zeobase.zbtechnical.challenges.type.ErrorCode;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 예약 관련 api 를 담는 Controller 클래스
 */
@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;


    /**
     * 개별 예약의 정보를 전달하는 api
     *
     * @param reservationId
     * @return
     */
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationInfoResponse> reservationInfo(
            @PathVariable Long reservationId
    ) {

        return ResponseEntity.ok().body(reservationService.getReservationInfoById(reservationId));
    }

    /**
     * 특정 매장의 모든 예약 정보를 전달하는 api
     *
     * @param storeId
     * @return
     */
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<ReservationInfoResponse>> reservationsInfo(
            @PathVariable Long storeId
    ) {

        return ResponseEntity.ok().body(reservationService.getReservationsInfoByStoreId(storeId));
    }

    /**
     * 특정 매장의 특정 시간에 예약이 가능한지 (이미 등록된 예약이 없는지) 확인하는 api
     *
     * @param storeId
     * @param reserveTime - 확인할 날짜 정보
     * @return
     */
    @GetMapping("/store/{storeId}/available")
    public ResponseEntity<ReservationAvailableResponse> validateReservationAvailable(
            @PathVariable Long storeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime reserveTime
    ) {

        return ResponseEntity.ok().body(reservationService.existsAvailableReservationTime(storeId, reserveTime));
    }

    /**
     * 실제 예약을 진행하는 api
     * 내부적으로 예약 가능 여부를 검증 후에 예약 진행
     *
     * @param request - 매장 정보, 예약 희망 날짜
     * @return
     */
    @PostMapping("/reserve")
    public ResponseEntity<ReservationReserveResponse> reserve(
            @Valid @RequestBody ReservationReserveRequest request,
            BindingResult bindingResult,
            Authentication authentication
    ) {

        if(bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();

            throw new StoreException(ErrorCode.INVALID_RESERVATION_REQUEST.modifyDescription(errors.get(0).getDefaultMessage()));
        }

        return ResponseEntity.ok().body(reservationService.reserve(request, authentication));
    }

    /**
     * 매장 점주가 특정 예약에 대해 승인/거절하는 api
     *
     * @param request - 예약 정보, 가게 정보, 승인/거절 정보
     * @param authentication - 토큰을 활용한 이용자(매장 주인) 검증
     * @return
     */
    @PostMapping("/accept")
    public ResponseEntity<ReservationAcceptResponse> accept(
            @RequestBody ReservationAcceptRequest request,
            Authentication authentication
    ) {

        return ResponseEntity.ok().body(reservationService.acceptReservationByStoreOwner(request, authentication));
    }
}
