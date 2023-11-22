package zeobase.zbtechnical.challenges.dto.member.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이용자의 회원 가입 관련 response DTO 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignupResponse {

    private Long memberId;
    private String UID;
}
