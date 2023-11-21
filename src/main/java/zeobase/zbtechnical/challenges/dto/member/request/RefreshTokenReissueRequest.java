package zeobase.zbtechnical.challenges.dto.member.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이용자의 refresh token 재발급 관련 request DTO 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenReissueRequest {

    private String refreshToken;
}
