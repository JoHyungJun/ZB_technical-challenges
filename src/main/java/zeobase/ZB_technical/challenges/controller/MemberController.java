package zeobase.ZB_technical.challenges.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import zeobase.ZB_technical.challenges.dto.common.ErrorResponse;
import zeobase.ZB_technical.challenges.dto.member.MemberInfoDto;
import zeobase.ZB_technical.challenges.dto.member.MemberSigninDto;
import zeobase.ZB_technical.challenges.dto.member.MemberSignupDto;
import zeobase.ZB_technical.challenges.exception.MemberException;
import zeobase.ZB_technical.challenges.service.MemberService;

import javax.validation.Valid;
import java.util.List;

import static zeobase.ZB_technical.challenges.type.ErrorCode.INVALID_SIGN_IN_REQUEST;

/**
 * 이용자 관련 api 를 담는 Controller 클래스
 */
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


    /**
     * 회원 가입을 진행하는 api
     *
     * @param request - 가입에 필요한 회원 정보
     * @param bindingResult
     * @return
     * @exception MemberException
     */
    @PostMapping("/signup")
    public ResponseEntity<MemberSignupDto.Response> signup(
            @Valid @RequestBody MemberSignupDto.Request request,
            BindingResult bindingResult
    ) {

        if(bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();

            throw new MemberException(INVALID_SIGN_IN_REQUEST.modifyDescription(errors.get(0).getDefaultMessage()));
        }

        return ResponseEntity.ok().body(memberService.signup(request));
    }

    /**
     * 로그인을 진행하는 api
     *
     * @param request - id, password
     * @return
     */
    @PostMapping("/signin")
    public ResponseEntity<MemberSigninDto.Response> signin(
            @RequestBody MemberSigninDto.Request request
    ) {

        return ResponseEntity.ok().body(memberService.signin(request));
    }

    /**
     * 개별 이용자의 공개 가능한 정보를 전달하는 api
     *
     * @param memberId
     * @return
     */
    @GetMapping("")
    public ResponseEntity<MemberInfoDto> userPublicInfo(
            @RequestParam("id") Long memberId
    ) {

        return ResponseEntity.ok().body(
                memberService.getMemberPublicInfoByMemberId(memberId));
    }
}
