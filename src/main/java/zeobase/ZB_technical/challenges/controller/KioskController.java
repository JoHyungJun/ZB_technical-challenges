package zeobase.ZB_technical.challenges.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zeobase.ZB_technical.challenges.dto.kiosk.KioskPhoneDto;
import zeobase.ZB_technical.challenges.service.KioskService;

import javax.validation.Valid;

@RestController
@RequestMapping("/kiosk")
@RequiredArgsConstructor
public class KioskController {

    private final KioskService kioskService;


    @PostMapping("/phone")
    public ResponseEntity<KioskPhoneDto.Response> checkReservationByPhone(@Valid @RequestBody KioskPhoneDto.Request request,
                                                                          BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(KioskPhoneDto.Response.builder()
                            .message(bindingResult.getFieldErrors()
                                    .get(0)
                                    .getDefaultMessage())
                            .build());
        }

        return ResponseEntity.ok(kioskService.checkReservationByPhone(request));
    }
}
