package zeobase.ZB_technical.challenges.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zeobase.ZB_technical.challenges.type.MemberRoleType;
import zeobase.ZB_technical.challenges.type.MemberStatusType;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberPublicInfoDto {

    private String memberId;
    private MemberRoleType memberRoleType;
    private MemberStatusType memberStatusType;
}
