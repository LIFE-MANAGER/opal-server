package com.lifeManager.opalyouth.service.security;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.dto.security.LoginRequest;
import com.lifeManager.opalyouth.dto.security.OpalPrincipal;
import com.lifeManager.opalyouth.dto.security.oauth2.KakaoOAuth2Response;
import com.lifeManager.opalyouth.dto.security.oauth2.NaverOAuth2Response;
import com.lifeManager.opalyouth.dto.security.oauth2.accessToken.KakaoOAuthToken;
import com.lifeManager.opalyouth.dto.security.oauth2.accessToken.NaverOAuthToken;
import com.lifeManager.opalyouth.entity.Member;
import com.lifeManager.opalyouth.repository.MemberRepository;
import com.lifeManager.opalyouth.utils.JwtUtils;
import com.lifeManager.opalyouth.utils.OAuth2Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static com.lifeManager.opalyouth.common.properties.JwtProperties.*;
import static com.lifeManager.opalyouth.common.response.BaseResponseStatus.*;
@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class LoginService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final OAuth2Utils oAuth2Utils;
    private final JwtUtils jwtUtils;

    private static final String KAKAO_ID_WITH_PRINCIPAL_PREFIX = "KAKAO_";
    private static final String NAVER_ID_WITH_PRINCIPAL_PREFIX = "NAVER_";


    public void localLogin(LoginRequest loginRequest, HttpServletResponse response) {
        log.info("loginRequest : {}", loginRequest.getEmail());
        Member member = memberRepository.findByEmailAndState(loginRequest.getEmail(), BaseEntity.State.ACTIVE)
                .orElseThrow(() -> new BaseException(NON_EXIST_USER));
        log.info("member email : {}", member.getEmail());
        log.info("member password : {}", member.getPassword());

        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new BaseException(WRONG_PASSWORD);
        }
        Map<String, String> tokens = jwtUtils.generateToken(member.getEmail());

        response.addHeader(JWT_ACCESS_TOKEN_HEADER_NAME, JWT_ACCESS_TOKEN_TYPE + tokens.get("accessToken"));

        Cookie refreshCookie = new Cookie(JWT_REFRESH_TOKEN_COOKIE_NAME, tokens.get("refreshToken"));
        refreshCookie.setMaxAge((int) REFRESH_TOKEN_EXPIRE_TIME); // 쿠키의 만료시간 설정
        refreshCookie.setPath("/");
        response.addCookie(refreshCookie);
    }


    public void kakaoLogin(String code, HttpServletResponse response) {

        KakaoOAuthToken kakaoOAuthToken = oAuth2Utils.requestKakaoOAuthToken(code);
        KakaoOAuth2Response kakaoOAuth2Response = oAuth2Utils.requestKakaoOAuth2Entity(kakaoOAuthToken.getAccess_token());

        String idWithProvider = KAKAO_ID_WITH_PRINCIPAL_PREFIX + kakaoOAuth2Response.getId().toString();
        // todo : 일단은 활성 회원만 조회하도록 로직 구현했으므로 휴면회원 처리 로직을 추가해야 한다.
        Optional<Member> optionalMember = memberRepository.findByIdWithProviderAndState(idWithProvider, BaseEntity.State.ACTIVE); // idWithProvider로 활성회원 조회

        if (optionalMember.isEmpty()) {
            // 가입된 회원이 없는 경우 클라이언트에게 "idWithProvider", "email", "username" 을 responseBody에 담아서 넘겨준다. 이후 클라이언트는 서버에 이 정보를 토대로 회원가입 포인트로 진입하여 회원가입을 완료시킨다.
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            MultiValueMap<String, String> responseBody = new LinkedMultiValueMap<>();
            responseBody.add("idWp", idWithProvider);
            responseBody.add("email", kakaoOAuth2Response.getEmail());
            responseBody.add("username", kakaoOAuth2Response.getNickname());

            try {
                response.getWriter().write(responseBody.toString());
                // todo : 프론트 엔드의 회원가입 페이지로 이동시키기
            } catch (IOException e) {
                throw new BaseException(INTERNAL_SERVER_ERROR);
            }
        } else {
            // 회원가입이 이미 되어있는 회원이라면 권한을 부여한다.
            grantAuthentication(response, optionalMember);
        }
    }


    public void naverLogin(String code, HttpServletResponse response) {

        NaverOAuthToken naverOAuthToken = oAuth2Utils.requestNaverOAuthToken(code);
        NaverOAuth2Response naverOAuth2Response = oAuth2Utils.requestNaverOAuthEntity(naverOAuthToken.getAccessToken());

        String idWithProvider = NAVER_ID_WITH_PRINCIPAL_PREFIX + naverOAuth2Response.getResponse().getId();
        // todo : 일단은 활성 회원만 조회하도록 로직 구현했으므로 휴면회원 처리 로직을 추가해야 한다.
        Optional<Member> optionalMember = memberRepository.findByIdWithProviderAndState(idWithProvider, BaseEntity.State.ACTIVE);

        if (optionalMember.isEmpty()) {

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            MultiValueMap<String, String> responseBody = new LinkedMultiValueMap<>();
            responseBody.add("idWp", idWithProvider);
            responseBody.add("username", naverOAuth2Response.getResponse().getName());

            try {
                response.getWriter().write(responseBody.toString());
                // todo : 프론트 엔드의 회원가입 페이지로 이동시키기
            } catch (IOException e) {
                throw new BaseException(INTERNAL_SERVER_ERROR);
            }
        } else {
            grantAuthentication(response, optionalMember);
        }
    }

    /**
     * Jwt 토큰을 생성하여 클라이언트에 보내고 루트페이지로 리다이렉트 시킨다.
     * @param response
     * @param optionalMember
     */
    private void grantAuthentication(HttpServletResponse response, Optional<Member> optionalMember) {
        // 권한 만들기
        OpalPrincipal opalPrincipal = OpalPrincipal.createOpalPrincipalByMemberEntity(optionalMember.get());
        // Jwt토큰 생성
        Map<String, String> token = jwtUtils.generateToken(opalPrincipal.getEmail());

        // Authorization Header에는 엑세스 토큰, 쿠키에는 Refresh Token을 담아 클라이언트에 전송
        response.addHeader(JWT_ACCESS_TOKEN_HEADER_NAME, JWT_ACCESS_TOKEN_TYPE + token.get("accessToken"));

        Cookie refreshCookie = new Cookie(JWT_REFRESH_TOKEN_COOKIE_NAME, token.get("refreshToken"));
        refreshCookie.setMaxAge((int) REFRESH_TOKEN_EXPIRE_TIME); // 쿠키의 만료시간 설정
        refreshCookie.setPath("/");
        response.addCookie(refreshCookie);
        try {
            // Jwt 토큰을 response 한 이후 루트 페이지로 Redirect 시킴
            response.sendRedirect("/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
