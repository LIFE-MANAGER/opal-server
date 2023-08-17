package com.lifeManager.opalyouth.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lifeManager.opalyouth.dto.security.oauth2.KakaoOAuth2Response;
import com.lifeManager.opalyouth.dto.security.oauth2.NaverOAuth2Response;
import com.lifeManager.opalyouth.dto.security.oauth2.accessToken.KakaoOAuthToken;
import com.lifeManager.opalyouth.dto.security.oauth2.accessToken.NaverOAuthToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class OAuth2Utils {
    private final String KAKAO_CLIENT_ID;
    private final String KAKAO_CLIENT_SECRET;
    private final String NAVER_CLIENT_ID;
    private final String NAVER_CLIENT_SECRET;
    private final String NAVER_STATE = "bmF2ZXJzdGF0ZW9wYWx5b3V0aA==";
    private final String AUTHORIZATION_CODE_GRANT_TYPE = "authorization_code";
    public OAuth2Utils(
            @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
            String kakao_client_id,
            @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
            String kakao_client_secret,
            @Value("${spring.security.oauth2.client.registration.naver.client-id}")
            String naver_client_id,
            @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
            String naver_client_secret
    ) {
        KAKAO_CLIENT_ID = kakao_client_id;
        KAKAO_CLIENT_SECRET = kakao_client_secret;
        NAVER_CLIENT_ID = naver_client_id;
        NAVER_CLIENT_SECRET = naver_client_secret;
    }

    /**
     * 카카오에게 받은 코드를 통하여 엑세스 토큰을 요청하는 로직
     * @param code
     * @return
     * @throws RuntimeException
     */
    public KakaoOAuthToken requestKakaoOAuthToken(String code) throws RuntimeException{
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", AUTHORIZATION_CODE_GRANT_TYPE);
        params.add("client_id", KAKAO_CLIENT_ID);
        params.add("redirect_uri", "http://118.67.143.157:8080/login/oauth2/code/kakao");
        params.add("code", code);
        params.add("client_secret", KAKAO_CLIENT_SECRET);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenReq = new HttpEntity<>(params, httpHeaders);
        ResponseEntity<String> res = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenReq,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoOAuthToken kakaoOAuthToken = null;
        try {
            kakaoOAuthToken = objectMapper.readValue(res.getBody(), KakaoOAuthToken.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return kakaoOAuthToken;
    }

    /**
     * 카카오에게 받은 엑세스토큰을 이용하여 카카오 리소스 서버로 사용자의 정보를 요청하는 로직
     * @param accessToken
     * @return
     * @throws RuntimeException
     */
    public KakaoOAuth2Response requestKakaoOAuth2Entity(String accessToken) throws RuntimeException{
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        httpHeaders.add("Authorization", "Bearer " + accessToken);

        HttpEntity<MultiValueMap<String, String>> kakaoProfileReq = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileReq,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        KakaoOAuth2Response kakaoOAuth2Response = null;
        try {
            kakaoOAuth2Response = objectMapper.readValue(response.getBody(), KakaoOAuth2Response.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return kakaoOAuth2Response;
    }

    public NaverOAuthToken requestNaverOAuthToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", "application/json");


        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", AUTHORIZATION_CODE_GRANT_TYPE);
        params.add("client_id", NAVER_CLIENT_ID);
        params.add("client_secret", NAVER_CLIENT_SECRET);
        params.add("code", code);
        params.add("state", NAVER_STATE);

        URI uri = UriComponentsBuilder.fromHttpUrl("https://nid.naver.com/oauth2.0/token")
                .queryParams(params)
                .build().toUri();

        HttpEntity<MultiValueMap<String, String>> naverTokenReq = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> res = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                naverTokenReq,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        NaverOAuthToken naverOAuthToken = null;
        try {
            naverOAuthToken = objectMapper.readValue(res.getBody(), NaverOAuthToken.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return naverOAuthToken;
    }

    public NaverOAuth2Response requestNaverOAuthEntity(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", "application/json");
        httpHeaders.add("Authorization", "Bearer " + accessToken);

        HttpEntity<MultiValueMap<String, String>> naverProfileReq = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                naverProfileReq,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        NaverOAuth2Response naverOAuth2Response = null;
        try {
            naverOAuth2Response = objectMapper.readValue(response.getBody(), NaverOAuth2Response.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return naverOAuth2Response;
    }
}
