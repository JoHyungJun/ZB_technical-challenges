package zeobase.zbtechnical.challenges.service;

import org.springframework.security.core.Authentication;
import zeobase.zbtechnical.challenges.dto.member.request.*;
import zeobase.zbtechnical.challenges.dto.member.response.*;

/**
 * 이용자 관련 Service 의 부모 인터페이스
 */
public interface MemberService {

    MemberInfoResponse getMemberPublicInfoByMemberId(Long memberId);
    MemberSignupResponse signup(MemberSignupRequest request);
    TokenResponse signin(MemberSigninRequest request);
    MemberSignOutResponse signout(Authentication authentication);
    MemberModifyResponse modify(MemberModifyRequest request, Authentication authentication);
    MemberWithdrawResponse withdraw(Authentication authentication);
    TokenResponse reissue(RefreshTokenReissueRequest request);
}
