package zeobase.zbtechnical.challenges.utils.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import zeobase.zbtechnical.challenges.dto.member.response.TokenResponse;
import zeobase.zbtechnical.challenges.exception.JwtException;
import zeobase.zbtechnical.challenges.type.ErrorCode;
import zeobase.zbtechnical.challenges.type.MemberRoleType;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

/**
 * Jwt 토큰 생성, 파싱 등 역할의 시큐리티 관련 Util 클래스
 */
@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final UserDetailsService userDetailsService;

    private static final Long ACCESS_TOKEN_EXPIRE_TIME = 3 * 60 * 60 * 1000L; // 3 Hours
    private static final Long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L; // 7 Days
    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Value("${spring.jwt.secret}")
    private String secretKey;
    private Key key;


    /**
     * 토큰 생성을 위해 초기화 작업을 도와주는 메서드
     */
    @PostConstruct
    protected void init() {

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 토큰을 생성하는 메서드
     * 
     * @param memberId - 이용자의 UID
     * @param memberRoleType - 이용자의 권한
     * @return 이용자 정보를 담은 토큰
     */
    public TokenResponse createToken(String memberId, MemberRoleType memberRoleType) {

        // 만료 일자 등의 설정을 위해 현재 발급 날짜 선언
        Date date = new Date();
        
        // access token 제작부
        Claims accessTokenClaims = Jwts.claims();
        accessTokenClaims.setSubject(memberId);
        accessTokenClaims.put("memberRoleType", memberRoleType);

        String accessToken = Jwts.builder()
                .setClaims(accessTokenClaims)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // refresh token 제작부
        // refresh token 에는 되도록이면 아무 정보가 들어가지 않는 것이 좋지만, 멤버 추출을 위해 memberId만 부여
        Claims refreshTokenClaims = Jwts.claims();
        refreshTokenClaims.setSubject(memberId);

        String refreshToken = Jwts.builder()
                .setClaims(refreshTokenClaims)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenResponse.builder()
                .grantType(TOKEN_PREFIX.substring(0, TOKEN_PREFIX.length()-1))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiredDate(date.getTime() + REFRESH_TOKEN_EXPIRE_TIME)
                .build();
    }

    /**
     * request 에서 토큰을 받아 prefix 인 "Bearer "을 제거하고 나머지 토큰을 전달하는 메서드
     *
     * @param request
     * @return "Bearer "을 제거한 토큰 - 비정상적인 토큰의 경우 null 반환
     */
    public String resolveTokenFromRequest(HttpServletRequest request) {

        String token = request.getHeader(TOKEN_HEADER);

        if(ObjectUtils.isEmpty(token) || !token.startsWith(TOKEN_PREFIX)) {
            return null;
        }

        return token.substring(TOKEN_PREFIX.length());
    }

    /**
     * 토큰의 만료 일자 등을 검증하는 메서드
     * 
     * @param token - 토큰
     * @return 토큰이 비어있거나, 만료 일자가 지난 토큰의 경우 false 반환
     */
    public boolean validateToken(String token) {

        if(ObjectUtils.isEmpty(token)) {
            return false;
        }

        return !parseClaimsByToken(token)
                .getExpiration()
                .before(new Date());
    }

    /**
     * 토큰 을 통해 이용자 Authentication 을 추출하는 메서드
     *
     * @param token - 토큰
     * @return 
     */
    public Authentication parseAuthenticationByToken(String token) {

        String memberId = (String) parseClaimsByToken(token).getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(memberId);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * 토큰을 파싱하며, 파싱 중에 생긴 에러를 감지하는 메서드
     * 
     * @param token - 토큰
     * @return 설정한 secret key 를 이용해 파싱한 Claims 를 반환
     */
    public Claims parseClaimsByToken(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new JwtException(ErrorCode.EXPIRED_JWT_EXCEPTION);
        }  catch (UnsupportedJwtException e) {
            throw new JwtException(ErrorCode.UNSUPPORTED_JWT_EXCEPTION);
        } catch (MalformedJwtException e) {
            throw new JwtException(ErrorCode.MALFORMED_JWT_EXCEPTION);
        } catch (SignatureException e) {
            throw new JwtException(ErrorCode.SIGNATURE_EXCEPTION);
        } catch (IllegalArgumentException e) {
            throw new JwtException(ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION);
        }
    }
}
