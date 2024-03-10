package me.seeun.blogdev.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.seeun.blogdev.domain.User;
import org.springframework.boot.SpringApplication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TokenProvider {

    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    // JWT 토큰 생성 메서드
    public String makeToken(Date expiry, User user) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 typ : JWT
                // iss : ajufresh@gmail.com
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now) // iat : 현재 시간
                .setExpiration(expiry) // exp : expiry 멤버 변수 값
                .setSubject(user.getEmail()) // sub : 유저의 email
                .claim("id", user.getId()) // 클레임 id: 유저 id
                //서명 : 비밀값과 함계 해시값을 HS256 방식으로 암호화
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretkey())
                .compact();
    }
    /*
        expiry: 만료시간
        user : 유저 정보
     */

    // JWT 토큰 유효성 검증 메서드
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretkey()) // 비밀 값으로 복호화
                    .parseClaimsJws(token);
            return true;
        }   catch (Exception e) { // 복호화 과정에서 에러가 나면 유효하지 않은 토큰
            return false;
        }
    }

    // 토큰 기반으로 인증 정보를 가져오는 메서드
    public Authentication getAuthentication(String token) {
        Claims claims =  getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new
                SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(claims.getSubject
                (), "", authorities), token, authorities);
    }
    /*
        클레임을 가져오는 private 메서드인 getClaims()를 호출하여 클래임 정보를 반환
        사용자 이메일이 들어 있는 토큰 제목 sub와 토근 기반으로 인증 정보 생성
        UsernamePasswordAuthenticationToken의 첫 인자로 들어가는 User는 spring security에서 제공하는 객체
     */

    // 토큰 기반으로 유저 id를 가져오는 메소드
    public Long getUserId(String token) {
        Claims claims =  getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parser() // 클레임 조회
                .setSigningKey(jwtProperties.getSecretkey())
                .parseClaimsJws(token)
                .getBody();
    }
}
