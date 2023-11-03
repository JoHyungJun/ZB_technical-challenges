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
import zeobase.ZB_technical.challenges.dto.reservation.ReservationAvailableDto;
import zeobase.ZB_technical.challenges.dto.reservation.ReservationDto;
import zeobase.ZB_technical.challenges.dto.reservation.ReservationInfoDto;
import zeobase.ZB_technical.challenges.service.ReservationService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

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
    public ResponseEntity<?> reserve(
            @Valid @RequestBody ReservationDto.Request request,
            BindingResult bindingResult,
            Authentication authentication
    ) {

        if(bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder()
                            .httpStatus(HttpStatus.BAD_REQUEST)
                            .errorCode("INVALID_RESERVATION_REQUEST")
                            .errorMessage(errors.get(0).getDefaultMessage())
                            .build());
        }

        return ResponseEntity.ok().body(reservationService.reserve(request, authentication));
    }
}
