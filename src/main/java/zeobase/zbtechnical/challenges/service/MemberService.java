package zeobase.zbtechnical.challenges.service;

import org.springframework.security.core.Authentication;
import zeobase.zbtechnical.challenges.dto.member.*;
import zeobase.zbtechnical.challenges.dto.member.request.MemberSigninRequest;
import zeobase.zbtechnical.challenges.dto.member.request.RefreshTokenReissueRequest;
import zeobase.zbtechnical.challenges.dto.member.response.MemberInfoResponse;
import zeobase.zbtechnical.challenges.dto.member.response.MemberSignOutResponse;
import zeobase.zbtechnical.challenges.dto.member.response.TokenResponse;

/**
 * 이용자 관련 Service 의 부모 인터페이스
 */
public interface MemberService {

    MemberSignup.Response signup(MemberSignup.Request request);
    TokenResponse signin(MemberSigninRequest request);
    MemberSignOutResponse signout(Authentication authentication);
    TokenResponse reissue(RefreshTokenReissueRequest request);
    MemberInfoResponse getMemberPublicInfoByMemberId(Long memberId);
}
