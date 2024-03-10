package me.seeun.blogdev.service;

import lombok.RequiredArgsConstructor;
import me.seeun.blogdev.domain.User;
import me.seeun.blogdev.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
// UserDetailsService : spring security에서 사용자 정보를 가져오는 interface
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    //email로 사용자의 정보를 가져오는 메서드
    @Override
    public User loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException((email)));
    }
}
