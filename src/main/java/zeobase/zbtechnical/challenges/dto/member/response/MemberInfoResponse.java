package zeobase.zbtechnical.challenges.dto.member.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zeobase.zbtechnical.challenges.entity.Member;
import zeobase.zbtechnical.challenges.type.member.MemberRoleType;
import zeobase.zbtechnical.challenges.type.member.MemberSignedStatusType;

/**
 * 공개된 이용자의 정보 관련 response DTO 클래스
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoResponse {

    private Long memberId;
    private String UID;
    private MemberRoleType role;
    private MemberSignedStatusType status;


    public static MemberInfoResponse fromEntity(Member member) {

        return MemberInfoResponse.builder()
                .memberId(member.getId())
                .UID(member.getUID())
                .role(member.getRole())
                .status(member.getStatus())
                .build();
    }
}
