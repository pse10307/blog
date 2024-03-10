package me.seeun.blogdev.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddUserRequest {
    private String email;
    private String password;
}
// 사용자 정보를 담고 있는 객체