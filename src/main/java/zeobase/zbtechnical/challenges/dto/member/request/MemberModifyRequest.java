package zeobase.zbtechnical.challenges.dto.member.request;

import lombok.Builder;
import lombok.Getter;
import zeobase.zbtechnical.challenges.type.MemberRoleType;

/**
 * 이용자의 정보 수정 관련 request DTO 클래스
 * 수정해야 하는 부분의 필드에만 값이 들어오고,
 * 수정하지 않을 부분은 null 로 들어오기 때문에 validate 를 검증하지 않는다.
 */
@Getter
@Builder
public class MemberModifyRequest {

    private String UID;
    private String password;
    private MemberRoleType role;
    private String name;
    private String phone;
}
