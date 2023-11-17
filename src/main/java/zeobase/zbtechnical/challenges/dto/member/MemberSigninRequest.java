package zeobase.zbtechnical.challenges.dto.member;

import lombok.Builder;
import lombok.Getter;

/**
 * 이용자의 로그인 관련 DTO 클래스
 */
@Getter
@Builder
public class MemberSigninRequest {

    private String UID;
    private String password;
}
