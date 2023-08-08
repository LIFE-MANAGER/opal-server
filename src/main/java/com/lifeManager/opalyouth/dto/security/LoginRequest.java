package com.lifeManager.opalyouth.dto.security;

import lombok.Getter;

@Getter
public class LoginRequest { // 로그인 요청 시 Dto
    private String email;
    private String password;
}
