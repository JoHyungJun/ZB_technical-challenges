package zeobase.ZB_technical.challenges.service;

import zeobase.ZB_technical.challenges.dto.member.MemberPublicInfoDto;
import zeobase.ZB_technical.challenges.dto.member.MemberSigninDto;
import zeobase.ZB_technical.challenges.dto.member.MemberSignupDto;

public interface MemberService {

    MemberSignupDto.Response signup(MemberSignupDto.Request request);
    MemberSigninDto.Response signin(MemberSigninDto.Request request);
    MemberPublicInfoDto getMemberPublicInfoByMemberId(String memberId);
}
