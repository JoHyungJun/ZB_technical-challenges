package zeobase.zbtechnical.challenges.dto.member;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberSignOutResponse {

    private boolean signoutSuccess;
}
