package me.seeun.blogdev.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties("jwt") //자바 클래스에서 property값을 가져와서 사용
public class JwtProperties {
    private String issuer;
    private String secretkey;
}
// JWT 토클을 만드려면 issuer, secret_key를 필수로 설정해야 함.