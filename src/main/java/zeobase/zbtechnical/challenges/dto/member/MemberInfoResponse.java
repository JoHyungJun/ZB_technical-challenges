package zeobase.zbtechnical.challenges.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zeobase.zbtechnical.challenges.type.MemberRoleType;
import zeobase.zbtechnical.challenges.type.MemberStatusType;

/**
 * 공개된 이용자의 정보 관련 DTO 클래스
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoResponse {

    private String UID;
    private MemberRoleType role;
    private MemberStatusType status;
}
