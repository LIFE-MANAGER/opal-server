package com.lifeManager.opalyouth.controller;

import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.common.response.BaseResponse;
import com.lifeManager.opalyouth.dto.security.LoginRequest;
import com.lifeManager.opalyouth.dto.security.oauth2.NaverOAuth2Response;
import com.lifeManager.opalyouth.dto.security.oauth2.accessToken.NaverOAuthToken;
import com.lifeManager.opalyouth.service.security.LoginService;
import com.lifeManager.opalyouth.utils.OAuth2Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@Slf4j
@RequiredArgsConstructor
@RestController
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/")
    public String test() {
        return "test";
    }

    @PostMapping("/login")
    public BaseResponse<String> localLogin(@Valid @RequestBody LoginRequest loginRequest, BindingResult result, HttpServletResponse response) {
        if (result.hasErrors()) {
            String message = result.getFieldError().getDefaultMessage();
            return new BaseResponse<>(false, 400, message);
        }
        try {
            loginService.localLogin(loginRequest, response);
            return new BaseResponse<>("로그인에 성공하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 카카오 로그인 시 redirect 받을 주소
     * @param code
     * @param response
     * @return
     */
    @GetMapping("/login/oauth2/code/kakao")
    public BaseResponse<Object> kakaoRedirect(@RequestParam String code, HttpServletResponse response) {
        try {
            loginService.kakaoLogin(code, response);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
        return null;
    }

    /**
     * 네이버 로그인 시 redirect 받을 주소
     * @param code
     * @param response
     * @return
     */
    @GetMapping("/login/oauth2/code/naver")
    public BaseResponse<Object> naverRedirect(@RequestParam String code, HttpServletResponse response) {
        try {
            loginService.naverLogin(code, response);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
        return null;
    }
}
