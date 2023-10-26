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

@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .httpBasic().disable()  // UI 인증 비활성화
                .csrf().disable()       // csrf 비활성화
                .cors().disable()       // cors 비활성화

                .authorizeRequests()    // 권한 요청 여부 설정
                .antMatchers(
                        "/member/signup",    // 회원가입 요청 비활성화
                        "/member/signin",               // 로그인 요청 비활성화
                        "/kiosk/phone",                 // 키오스크 인증 요청 비활성화
                        "/kiosk/signin")
                .permitAll()

                .antMatchers("/**")           // 이외의 모든 요청 권한 필요
                .authenticated()

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 없이 jwt으로 독립적인 인증 수행

                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                ;

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
