package zeobase.ZB_technical.challenges.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이용자의 로그인 관련 DTO 클래스
 */
public class MemberSigninDto {

    @Getter
    @Builder
    public static class Request {

        private String memberId;
        private String password;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private String token;
    }
}
