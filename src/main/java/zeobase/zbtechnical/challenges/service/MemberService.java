package zeobase.zbtechnical.challenges.service;

import org.springframework.security.core.Authentication;
import zeobase.zbtechnical.challenges.dto.member.*;

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
