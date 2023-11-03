package zeobase.ZB_technical.challenges.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import zeobase.ZB_technical.challenges.dto.common.ErrorResponse;
import zeobase.ZB_technical.challenges.dto.member.MemberPublicInfoDto;
import zeobase.ZB_technical.challenges.dto.member.MemberSigninDto;
import zeobase.ZB_technical.challenges.dto.member.MemberSignupDto;
import zeobase.ZB_technical.challenges.service.MemberService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @Valid @RequestBody MemberSignupDto.Request request,
            BindingResult bindingResult
    ) {

        if(bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder()
                            .httpStatus(HttpStatus.BAD_REQUEST)
                            .errorCode("INVALID_SIGN_IN_REQUEST")
                            .errorMessage(errors.get(0).getDefaultMessage())
                            .build());
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
    public ResponseEntity<MemberPublicInfoDto> userPublicInfo(
            @RequestParam("id") String memberId
    ) {

        return ResponseEntity.ok().body(
                memberService.getMemberPublicInfoByMemberId(memberId));
    }
}
