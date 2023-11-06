package zeobase.ZB_technical.challenges.utils.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import io.jsonwebtoken.io.Decoders;
import org.springframework.util.ObjectUtils;
import zeobase.ZB_technical.challenges.exception.JwtException;
import zeobase.ZB_technical.challenges.exception.MemberException;
import zeobase.ZB_technical.challenges.service.Impl.UserDetailsServiceImpl;
import zeobase.ZB_technical.challenges.type.MemberRoleType;

import java.security.Key;
import java.util.Date;

import static zeobase.ZB_technical.challenges.type.ErrorCode.*;

/**
 * Jwt 토큰 생성, 파싱 등 역할의 시큐리티 관련 Util 클래스
 */
@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final UserDetailsService userDetailsService;

    // 토큰 만료 시간 - 3 DAYS
    private static final Long TOKEN_EXPIRE_TIME = 3 * 60 * 60 * 1000L;
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
    public String createToken(String memberId, MemberRoleType memberRoleType) {

        Claims claims = Jwts.claims();
        claims.setSubject(memberId);
        claims.put("memberRoleType", memberRoleType);

        Date date = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
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
            throw new JwtException(EXPIRED_JWT_EXCEPTION);
        }  catch (UnsupportedJwtException e) {
            throw new JwtException(UNSUPPORTED_JWT_EXCEPTION);
        } catch (MalformedJwtException e) {
            throw new JwtException(MALFORMED_JWT_EXCEPTION);
        } catch (SignatureException e) {
            throw new JwtException(SIGNATURE_EXCEPTION);
        } catch (IllegalArgumentException e) {
            throw new JwtException(ILLEGAL_ARGUMENT_EXCEPTION);
        }
    }
}
