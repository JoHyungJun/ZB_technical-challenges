package zeobase.zbtechnical.challenges.dto.member.request;

import lombok.Builder;
import lombok.Getter;
import zeobase.zbtechnical.challenges.type.member.MemberRoleType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static zeobase.zbtechnical.challenges.type.common.ErrorMessage.INVALID_PASSWORD_LENGTH_MSG;
import static zeobase.zbtechnical.challenges.type.common.ErrorMessage.INVALID_PASSWORD_REGEX_MSG;
import static zeobase.zbtechnical.challenges.type.common.ErrorMessage.INVALID_PHONE_REGEX_MSG;
import static zeobase.zbtechnical.challenges.utils.ValidateConstants.MAX_PHONE_LENGTH;
import static zeobase.zbtechnical.challenges.utils.ValidateConstants.MIN_PHONE_LENGTH;
import static zeobase.zbtechnical.challenges.utils.ValidateConstants.PASSWORD_REGEX;
import static zeobase.zbtechnical.challenges.utils.ValidateConstants.PHONE_REGEX;

/**
 * 이용자의 회원 가입 관련 request DTO 클래스
 */
@Getter
@Builder
public class MemberSignupRequest {

    @NotNull(message = "사용자의 아이디가 누락되었습니다.")
    private String UID;

    @Pattern(regexp = PASSWORD_REGEX,
            message = INVALID_PASSWORD_REGEX_MSG)
    @Size(min = MIN_PHONE_LENGTH, max = MAX_PHONE_LENGTH,
            message = INVALID_PASSWORD_LENGTH_MSG)
    private String password;

    @NotNull(message = "사용자의 권한이 누락되었습니다.")
    private MemberRoleType role;

    @NotNull(message = "사용자의 이름이 누락되었습니다.")
    private String name;

    @Pattern(regexp = PHONE_REGEX,
            message = INVALID_PHONE_REGEX_MSG)
    private String phone;
}
