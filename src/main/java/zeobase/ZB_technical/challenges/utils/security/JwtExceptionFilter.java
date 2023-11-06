package zeobase.ZB_technical.challenges.utils.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import zeobase.ZB_technical.challenges.dto.common.ErrorResponse;
import zeobase.ZB_technical.challenges.exception.JwtException;
import zeobase.ZB_technical.challenges.type.ErrorCode;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static zeobase.ZB_technical.challenges.type.ErrorCode.*;

/**
 * Jwt 토큰 검증 역할의 시큐리티 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;


    /**
     * JwtAuthenticationFilter 의 앞단에서 토큰 관련 에러를 감지하고,
     * 자체적으로 ErrorResponse 형식에 맞게 프론트 단으로 응답을 전송
     *
     * @param request
     * @param response
     * @param filterChain
     * @exception RuntimeException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            if (Objects.equals(e.getCode(), EXPIRED_JWT_EXCEPTION.name())) {
                sendErrorResponse(response, EXPIRED_JWT_EXCEPTION);
            }
            else if (Objects.equals(e.getCode(), UNSUPPORTED_JWT_EXCEPTION.name())) {
                sendErrorResponse(response, UNSUPPORTED_JWT_EXCEPTION);
            }
            else if (Objects.equals(e.getCode(), MALFORMED_JWT_EXCEPTION.name())) {
                sendErrorResponse(response, MALFORMED_JWT_EXCEPTION);
            }
            else if (Objects.equals(e.getCode(), SIGNATURE_EXCEPTION.name())) {
                sendErrorResponse(response, SIGNATURE_EXCEPTION);
            }
            else if (Objects.equals(e.getCode(), ILLEGAL_ARGUMENT_EXCEPTION.name())) {
                sendErrorResponse(response, ILLEGAL_ARGUMENT_EXCEPTION);
            }
        }
    }

    /**
     * 컨트롤러의 앞단 (Filter) 에서 자체적으로 ErrorResponse 형태에 맞게 응답하는 메서드
     *
     * @param response
     * @param errorCode
     * @exception RuntimeException
     */
    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws RuntimeException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(
                new ErrorResponse(errorCode)
        ));
    }
}
