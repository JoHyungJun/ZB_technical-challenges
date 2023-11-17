package zeobase.zbtechnical.challenges.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
