package zeobase.ZB_technical.challenges.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import zeobase.ZB_technical.challenges.dto.common.ErrorResponse;
import zeobase.ZB_technical.challenges.dto.reservation.ReservationAcceptDto;
import zeobase.ZB_technical.challenges.dto.reservation.ReservationAvailableDto;
import zeobase.ZB_technical.challenges.dto.reservation.ReservationReserveDto;
import zeobase.ZB_technical.challenges.dto.reservation.ReservationInfoDto;
import zeobase.ZB_technical.challenges.exception.StoreException;
import zeobase.ZB_technical.challenges.service.ReservationService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static zeobase.ZB_technical.challenges.type.ErrorCode.INVALID_RESERVATION_REQUEST;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;


    @GetMapping("")
    public ResponseEntity<List<ReservationInfoDto>> reservationsInfo(
            @RequestParam Long storeId
    ) {

        return ResponseEntity.ok().body(reservationService.getReservationsInfoByStoreId(storeId));
    }

    @GetMapping("/available")
    public ResponseEntity<ReservationAvailableDto> validateReservationAvailable(
            @RequestParam Long storeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime reservationTime
    ) {

        return ResponseEntity.ok().body(reservationService.existsAvailableReservationTime(storeId, reservationTime));
    }

    @PostMapping("/reserve")
    public ResponseEntity<ReservationReserveDto.Response> reserve(
            @Valid @RequestBody ReservationReserveDto.Request request,
            BindingResult bindingResult,
            Authentication authentication
    ) {

        if(bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();

            throw new StoreException(INVALID_RESERVATION_REQUEST.modifyDescription(errors.get(0).getDefaultMessage()));
        }

        return ResponseEntity.ok().body(reservationService.reserve(request, authentication));
    }

    @PostMapping("/accept")
    public ResponseEntity<ReservationAcceptDto.Response> accept(
            ReservationAcceptDto.Request request,
            Authentication authentication
    ) {

        return ResponseEntity.ok().body(reservationService.acceptReservationByStoreOwner(request, authentication));
    }
}
