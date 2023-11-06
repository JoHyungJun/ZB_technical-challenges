package zeobase.ZB_technical.challenges.service;

import zeobase.ZB_technical.challenges.dto.member.MemberInfoDto;
import zeobase.ZB_technical.challenges.dto.member.MemberSigninDto;
import zeobase.ZB_technical.challenges.dto.member.MemberSignupDto;

/**
 * 이용자 관련 Service 의 부모 인터페이스
 */
public interface MemberService {

    MemberSignupDto.Response signup(MemberSignupDto.Request request);
    MemberSigninDto.Response signin(MemberSigninDto.Request request);
    MemberInfoDto getMemberPublicInfoByMemberId(Long memberId);
}
