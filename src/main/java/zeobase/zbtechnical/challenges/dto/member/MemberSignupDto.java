package zeobase.zbtechnical.challenges.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zeobase.zbtechnical.challenges.type.MemberRoleType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 이용자의 회원 가입 관련 DTO 클래스
 */
public class MemberSignupDto {

    @Getter
    @Builder
    public static class Request {

        @NotNull(message = "사용자의 아이디가 누락되었습니다.")
        private String UID;

        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?!.*\\s).+$",
                 message = "비밀번호는 영어와 숫자를 혼용해야 하며 공백은 사용할 수 없습니다.")
        @Size(min = 8, max = 16, message = "비밀번호는 최소 8글자 이상 최대 16글자 이하로 작성해야 합니다.")
        private String password;

        @NotNull(message = "사용자의 권한이 누락되었습니다.")
        private MemberRoleType role;

        @NotNull(message = "사용자의 이름이 누락되었습니다.")
        private String name;

        @Pattern(regexp = "^01([0|1|6|7|8|9])([0-9]{3,4})([0-9]{4})",
                 message = "사용자의 핸드폰 번호 형식이 부적합합니다.")
        private String phone;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private Long memberId;
        private String UID;
    }
}
