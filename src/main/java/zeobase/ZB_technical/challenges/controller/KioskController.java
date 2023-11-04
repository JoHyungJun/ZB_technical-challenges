package zeobase.ZB_technical.challenges.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zeobase.ZB_technical.challenges.dto.kiosk.KioskPhoneDto;
import zeobase.ZB_technical.challenges.exception.ReviewException;
import zeobase.ZB_technical.challenges.service.KioskService;

import javax.validation.Valid;
import java.util.List;

import static zeobase.ZB_technical.challenges.type.ErrorCode.INVALID_KIOSK_REQUEST;
import static zeobase.ZB_technical.challenges.type.ErrorCode.INVALID_REVIEW_REQUEST;

@RestController
@RequestMapping("/kiosk")
@RequiredArgsConstructor
public class KioskController {

    private final KioskService kioskService;


    @PostMapping("/phone")
    public ResponseEntity<KioskPhoneDto.Response> checkReservationByPhone(
            @Valid @RequestBody KioskPhoneDto.Request request,
            BindingResult bindingResult
    ) {

        if(bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();

            throw new ReviewException(INVALID_KIOSK_REQUEST.modifyDescription(errors.get(0).getDefaultMessage()));
        }

        return ResponseEntity.ok(kioskService.checkReservationByPhone(request));
    }
}
