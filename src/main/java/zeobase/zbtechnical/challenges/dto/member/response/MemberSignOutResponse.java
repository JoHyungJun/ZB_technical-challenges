package zeobase.zbtechnical.challenges.dto.member.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 이용자의 로그아웃 관련 response DTO 클래스
 */
@Getter
@Builder
public class MemberSignOutResponse {

    private boolean signoutSuccess;
}
