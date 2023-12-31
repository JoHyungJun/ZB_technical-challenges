package zeobase.ZB_technical.challenges.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zeobase.ZB_technical.challenges.type.MemberRoleType;
import zeobase.ZB_technical.challenges.type.MemberStatusType;

/**
 * 공개된 이용자의 정보 관련 DTO 클래스
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoDto {

    private String memberId;
    private MemberRoleType memberRoleType;
    private MemberStatusType memberStatusType;
}
