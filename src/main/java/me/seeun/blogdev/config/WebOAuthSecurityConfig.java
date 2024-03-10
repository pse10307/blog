package me.seeun.blogdev.config;

import lombok.RequiredArgsConstructor;
import me.seeun.blogdev.config.jwt.TokenProvider;
import me.seeun.blogdev.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import me.seeun.blogdev.config.oauth.OAuth2SuccessHandler;
import me.seeun.blogdev.config.oauth.OAuth2UserCustomService;
import me.seeun.blogdev.repository.RefreshTokenRepository;
import me.seeun.blogdev.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Bean
    public WebSecurityCustomizer configure() {  // 스프링 시큐리티 기능 활설화.
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/img/**", "/css/**", "/js/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(cs -> cs.disable())
                .httpBasic(hb -> hb.disable())
                .formLogin((formLogin) -> formLogin.disable())
                .logout((logout) -> logout.disable());
        // 토큰 방식으로 인증을 하기 때문에 기존에 사용된 폼로그인, 세션 비활성화.

        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        /*
            addFilterBefore() : 헤더를 확인할 커스텀 필터 추가
            TokenAuthenticationFilter 클래스에서 구현함.
         */

        http.authorizeRequests()
                .requestMatchers("/api/token").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll();
            // 토큰 재발급 URL은 인증 없이 접근 가능하도록 설정. 나머지는 API URL은 인증 필요.

        http.oauth2Login( (oauth2Login) ->oauth2Login.loginPage("/login")
                .authorizationEndpoint((authend) ->
                        authend.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                // Authorization 요청과 관련된 상태 저장 -> 세션이 아닌 쿠키에 저장하기 위해 oAuth2AuthorizationRequestBasedOnCookieRepository에 저장
                .successHandler(oAuth2SuccessHandler())
                // 인증 성공 시 실행할 핸들러
                .userInfoEndpoint((userInfoEnd) -> userInfoEnd.userService(oAuth2UserCustomService)));


        http.logout((logout) ->
                logout.logoutSuccessUrl("/login"));


        http.exceptionHandling((exhand) ->exhand.defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                new AntPathRequestMatcher("/api/**")));
        /*
            6) /api로 시작하는 url인 경우 401 상태 코드를 반환하도록 예외 처리
                why? /api/token을 제외한 url인 경우, 인증이 필요하기 때문에
         */

        return http.build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService
        );
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
