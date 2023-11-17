package zeobase.zbtechnical.challenges.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import zeobase.zbtechnical.challenges.dto.member.*;
import zeobase.zbtechnical.challenges.exception.MemberException;
import zeobase.zbtechnical.challenges.service.MemberService;
import zeobase.zbtechnical.challenges.type.ErrorCode;

import javax.validation.Valid;
import java.util.List;

/**
 * 이용자 관련 api 를 담는 Controller 클래스
 */
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


    /**
     * 개별 이용자의 공개 가능한 정보를 전달하는 api
     *
     * @param memberId
     * @return
     */
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberInfoResponse> userPublicInfo(
            @PathVariable Long memberId
    ) {

        return ResponseEntity.ok().body(
                memberService.getMemberPublicInfoByMemberId(memberId));
    }

    /**
     * 회원 가입을 진행하는 api
     *
     * @param request - 가입에 필요한 회원 정보
     * @param bindingResult
     * @return
     * @exception MemberException
     */
    @PostMapping("/signup")
    public ResponseEntity<MemberSignup.Response> signup(
            @Valid @RequestBody MemberSignup.Request request,
            BindingResult bindingResult
    ) {

        if(bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();

            throw new MemberException(ErrorCode.INVALID_SIGN_IN_REQUEST.modifyDescription(errors.get(0).getDefaultMessage()));
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
    public ResponseEntity<TokenResponse> signin(
            @RequestBody MemberSigninRequest request
    ) {

        return ResponseEntity.ok().body(memberService.signin(request));
    }

    @PostMapping("/signout")
    public ResponseEntity<MemberSignOutResponse> signout(
            Authentication authentication
    ) {

        return ResponseEntity.ok().body(memberService.signout(authentication));
    }

    /**
     * 토큰 만료 시 재발급(refresh token)을 진행하는 api
     *
     * @param request - refresh token
     * @return
     */
    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(
            @RequestBody RefreshTokenReissueRequest request
    ) {

        return ResponseEntity.ok().body(memberService.reissue(request));
    }
}
