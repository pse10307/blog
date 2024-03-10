//package me.seeun.blogdev.config;
//
//import lombok.RequiredArgsConstructor;
//import me.seeun.blogdev.service.UserDetailService;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.ProviderManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//
//import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
//
//@RequiredArgsConstructor
//@Configuration
//@EnableWebSecurity
//public class WebSecurityConfig {
//    private final UserDetailsService userService;
//
//    @Bean
//    public WebSecurityCustomizer configure() {
//        return (web) -> web.ignoring()
//                .requestMatchers(toH2Console())
//                .requestMatchers("/static/**");
//    }
//    /*
//        스프링 시큐리티 기능 비활성화
//        일반적으로 정적 리소스에 설정(static 하위 경로 리소스)
//        .ignoring() 메서드 사용
//     */
//
//    @Bean // 특정 HTTP 요청에 대한 웹 기반 보안 구성
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http
//                .authorizeRequests((authorizeRequests) -> // 인증, 인가 설정
//                        authorizeRequests
//                                .requestMatchers("/login", "/signup", "/user").permitAll()
//                                .anyRequest().authenticated()
//                )
//                /*
//                    requestMatchers(): 특정 요청과 일치하는 url에 대한 액세스 설정
//                    permitAll(): 누구나 접근이 가능하게 설정
//                    -> "/login", "/signup", "/user"로 요청이 오면 인증/인가 없이도 접근할 수 있음.
//                    .anyRequest(): requestMatchers()로 설정한 url 이외의 요청에 대해 설정.
//                    .authenticated(): 별도의 인증 필요X but, 인증이 성공한 상태에서 접근 가능.
//                */
//                .formLogin((formLogin) ->  // 폼 기반 로그인 설정
//                        formLogin
//                                .loginPage("/login")
//                                .defaultSuccessUrl("/articles")
//
//                )
//                .logout((logoutConfig) -> // 로그아웃 설정
//                        logoutConfig.logoutSuccessUrl("/login")
//                                .invalidateHttpSession(true)
//                )
//                .csrf((csrfConfig) -> // 6) csrf 비활성화
//                        csrfConfig.disable()
//                )
//                .build();
//    }
//
//
//    // 인증 관리자 관련 설정
//    @Bean
//    public AuthenticationManager authenticationManager() throws Exception{
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userService);
//        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
//        return new ProviderManager(authProvider);
//    }
//
//    /*
//        사용자 서비스 설정
//        userDetailsService(): 설정하는 서비스 클래스는 반드시 UserDetailsService를 상속받은 클래스여야 함.
//        passwordEncoder(): 비밀번호 암호화를 위한 encoder 설정
//     */
//
//    // 패스워드 인코더로 사용할 빈 등록
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}