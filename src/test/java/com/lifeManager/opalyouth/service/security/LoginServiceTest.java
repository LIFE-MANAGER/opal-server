package com.lifeManager.opalyouth.service.security;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.common.properties.JwtProperties;
import com.lifeManager.opalyouth.common.response.BaseResponseStatus;
import com.lifeManager.opalyouth.dto.security.LoginRequest;
import com.lifeManager.opalyouth.entity.Member;
import com.lifeManager.opalyouth.repository.MemberRepository;
import com.lifeManager.opalyouth.utils.JwtUtils;
import com.lifeManager.opalyouth.utils.OAuth2Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {
    @Mock private HttpServletResponse httpServletResponse;
    @Mock private MemberRepository memberRepository;
    @Mock private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock private OAuth2Utils oAuth2Utils;
    @Mock private JwtUtils jwtUtils;
    @InjectMocks private LoginService loginService;

    @Test
    public void givenValidCredentials_whenLocalLogin_thenReturnJwtToken() {
        // given
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        Map<String, String> jwtToken = new HashMap<>();
        jwtToken.put("accessToken", "test_access_token");
        jwtToken.put("refreshToken", "test_refresh_token");

        String email = "test_email";
        String password = "test_password";
        String encodedPwd = encoder.encode(password);

        LoginRequest loginRequest = new LoginRequest(email, password);
        Member savedMember = Member.builder().email(email).password(encodedPwd).build();

        when(memberRepository.findByEmailAndState(email, BaseEntity.State.ACTIVE))
                .thenReturn(Optional.ofNullable(savedMember));
        assert savedMember != null;
        when(bCryptPasswordEncoder.matches(password, savedMember.getPassword()))
                .thenReturn(true);
        when(jwtUtils.generateToken(savedMember)).thenReturn(jwtToken);

        // when
        String res = loginService.localLogin(loginRequest, httpServletResponse);

        // then
        assertThat(res).isEqualTo("test_access_token");
        Mockito.verify(httpServletResponse).addCookie(argThat(cookie ->
                cookie.getName().equals(JwtProperties.JWT_REFRESH_TOKEN_COOKIE_NAME) &&
                cookie.getValue().equals("test_refresh_token") &&
                cookie.getMaxAge() == JwtProperties.REFRESH_TOKEN_EXPIRE_TIME &&
                cookie.getPath().equals("/")
        ));
    }

    @Test
    public void givenInvalidEmail_whenLocalLogin_thenThrowException() {
        // given
        String email = "test_email";
        String password = "test_password";

        LoginRequest loginRequest = new LoginRequest(email, password);

        when(memberRepository.findByEmailAndState(email, BaseEntity.State.ACTIVE))
                .thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> loginService.localLogin(loginRequest, httpServletResponse))
                .isInstanceOf(BaseException.class)
                .hasMessage(BaseResponseStatus.NON_EXIST_USER.getMessage())
                .hasFieldOrPropertyWithValue("status", BaseResponseStatus.NON_EXIST_USER);
    }

    @Test
    public void givenWrongPassword_whenLocalLogin_thenThrowException() {
        // given
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String email = "test_email";
        String password = "input_password";
        String encodedPwd = encoder.encode("saved_password");

        LoginRequest loginRequest = new LoginRequest(email, password);
        Member savedMember = Member.builder().email(email).password(encodedPwd).build();

        when(memberRepository.findByEmailAndState(email, BaseEntity.State.ACTIVE))
                .thenReturn(Optional.ofNullable(savedMember));
        assert savedMember != null;
        when(bCryptPasswordEncoder.matches(password, savedMember.getPassword()))
                .thenReturn(false);

        // when, then
        assertThatThrownBy(() -> loginService.localLogin(loginRequest, httpServletResponse))
                .isInstanceOf(BaseException.class)
                .hasMessage(BaseResponseStatus.WRONG_PASSWORD.getMessage())
                .hasFieldOrPropertyWithValue("status", BaseResponseStatus.WRONG_PASSWORD);
    }
}
