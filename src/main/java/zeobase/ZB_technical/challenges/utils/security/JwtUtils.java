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
import zeobase.ZB_technical.challenges.service.Impl.UserDetailsServiceImpl;
import zeobase.ZB_technical.challenges.type.MemberRoleType;

import java.security.Key;
import java.util.Date;

import static zeobase.ZB_technical.challenges.type.ErrorCode.*;

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


    // secret key 초기화
    @PostConstruct
    protected void init() {

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 제작
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

    // servlet request를 받아 header 부에서 'Authorization'을 찾아, 'Bearer '를 제거한 나머지 문자열 반환
    public String resolveTokenFromRequest(HttpServletRequest request) {

        String token = request.getHeader(TOKEN_HEADER);

        if(ObjectUtils.isEmpty(token) || !token.startsWith(TOKEN_PREFIX)) {
            return null;
        }

        return token.substring(TOKEN_PREFIX.length());
    }

    // 토큰 만료 검증
    public boolean validateToken(String token) {

        if(ObjectUtils.isEmpty(token)) {
            return false;
        }

        return !parseClaimsByToken(token)
                .getExpiration()
                .before(new Date());
    }

    // 토큰으로 Authentication 가져오기
    public Authentication parseAuthenticationByToken(String token) {

        String memberId = (String) parseClaimsByToken(token).getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(memberId);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰 에러 검증
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
