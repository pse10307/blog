package me.seeun.blogdev.service;

import lombok.RequiredArgsConstructor;
import me.seeun.blogdev.domain.RefreshToken;
import me.seeun.blogdev.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }
}

// 전달받은 리프레시 토큰으로 리프레시 토큰 객체를 검색해 전달하는 메서드