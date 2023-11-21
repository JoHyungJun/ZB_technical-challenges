package zeobase.zbtechnical.challenges.dto.member.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이용자의 token 발급 관련 response DTO 클래스
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long expiredDate;
}
