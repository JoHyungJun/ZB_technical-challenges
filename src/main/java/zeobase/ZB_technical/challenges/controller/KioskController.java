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
import zeobase.ZB_technical.challenges.dto.kiosk.KioskSigninDto;
import zeobase.ZB_technical.challenges.exception.ReviewException;
import zeobase.ZB_technical.challenges.service.KioskService;

import javax.validation.Valid;
import java.util.List;

import static zeobase.ZB_technical.challenges.type.ErrorCode.INVALID_KIOSK_REQUEST;
import static zeobase.ZB_technical.challenges.type.ErrorCode.INVALID_REVIEW_REQUEST;

/**
 * 키오스크 관련 api 를 담는 Controller 클래스
 */
@RestController
@RequestMapping("/kiosk")
@RequiredArgsConstructor
public class KioskController {

    private final KioskService kioskService;


    /**
     * 핸드폰 번호로 키오스크에서 방문 확인을 진행하는 api
     *
     * @param request - 핸드폰 번호, 매장 정보, 예약 정보
     * @param bindingResult
     * @return
     * @exception ReviewException
     */
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

    /**
     * 이용자 id, password로 키오스크에서 방문 확인을 진행하는 api
     *
     * @param request - id, password, 매장 정보, 예약 정보
     * @return
     */
    @PostMapping("/member")
    public ResponseEntity<KioskSigninDto.Response> checkReservationByMember(
            @RequestBody KioskSigninDto.Request request
    ) {

        return ResponseEntity.ok(kioskService.checkReservationByMember(request));
    }
}
