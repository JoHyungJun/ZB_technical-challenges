package zeobase.zbtechnical.challenges.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zeobase.zbtechnical.challenges.dto.kiosk.request.*;
import zeobase.zbtechnical.challenges.dto.kiosk.response.*;
import zeobase.zbtechnical.challenges.exception.ReviewException;
import zeobase.zbtechnical.challenges.service.KioskService;
import zeobase.zbtechnical.challenges.type.common.ErrorCode;

import javax.validation.Valid;
import java.util.List;

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
    public ResponseEntity<KioskPhoneResponse> checkReservationByPhone(
            @Valid @RequestBody KioskPhoneRequest request,
            BindingResult bindingResult
    ) {

        if(bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();

            throw new ReviewException(ErrorCode.INVALID_KIOSK_REQUEST.modifyDescription(errors.get(0).getDefaultMessage()));
        }

        return ResponseEntity.ok(kioskService.checkReservationByPhone(request));
    }

    /**
     * 이용자 id, password로 키오스크에서 방문 확인을 진행하는 api
     *
     * @param request - id, password, 매장 정보, 예약 정보
     * @return
     */
    @PostMapping("/signin")
    public ResponseEntity<KioskSigninResponse> checkReservationByMember(
            @RequestBody KioskSigninRequest request
    ) {

        return ResponseEntity.ok(kioskService.checkReservationByMember(request));
    }
}
