package zeobase.ZB_technical.challenges.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import zeobase.ZB_technical.challenges.utils.security.JwtAuthenticationFilter;
import zeobase.ZB_technical.challenges.utils.security.JwtExceptionFilter;

/**
 * 시큐리티 설정 관련 Config 클래스
 */
@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;


    /**
     * 시큐리티 필터 체인 설정 관련 메서드
     *
     * @param httpSecurity
     * @return
     * @exception Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .httpBasic().disable()  // UI 인증 비활성화
                .csrf().disable()       // csrf 비활성화
                .cors().disable()       // cors 비활성화

                .authorizeRequests()    // 권한 요청 여부 설정
                .antMatchers("/**")
                .permitAll()

                .anyRequest()         // 이외의 모든 요청 권한 필요
                .authenticated()

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 없이 jwt으로 독립적인 인증 수행

                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class)
                ;

        return httpSecurity.build();
    }

    /**
     * 이용자의 password 암호화 관련 bean 을 반환하는 메서드
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
