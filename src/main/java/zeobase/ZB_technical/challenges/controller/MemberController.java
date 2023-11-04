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

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


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

    @PostMapping("/signin")
    public ResponseEntity<MemberSigninDto.Response> signin(
            @RequestBody MemberSigninDto.Request request
    ) {

        return ResponseEntity.ok().body(memberService.signin(request));
    }

    @PostMapping("/signout")
    public ResponseEntity signout() {
        // TODO : refresh token을 구현할 경우 DB에서 지우는 작업 구현

        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<MemberInfoDto> userPublicInfo(
            @RequestParam("id") Long memberId
    ) {

        return ResponseEntity.ok().body(
                memberService.getMemberPublicInfoByMemberId(memberId));
    }
}
