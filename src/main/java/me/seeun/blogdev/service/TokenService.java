package me.seeun.blogdev.service;

import lombok.RequiredArgsConstructor;
import me.seeun.blogdev.config.jwt.TokenProvider;
import me.seeun.blogdev.domain.User;
import org.springframework.stereotype.Service;

import java.time.Duration;


@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public String createNewAccessToken(String refreshToken) {
        if(!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("(Unexpected token");
        }

        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user =  userService.findById(userId);

        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }
    /*
        refresh token으로 토큰 유효성 검사 후, 유효한 토큰일 경우  refresh token으로 userId를 검색
        userId로 user를 찾을 수 generateToken()메소드를 호출하여 새로운 액세스 토큰을 생성
     */
}
