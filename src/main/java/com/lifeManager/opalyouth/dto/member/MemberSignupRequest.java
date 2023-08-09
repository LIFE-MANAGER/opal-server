package com.lifeManager.opalyouth.dto.member;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import java.time.LocalDate;


@Getter
public class MemberSignupRequest { // 회원가입 요청 Dto
    @NotBlank private String nickname;

    private String idWithProvider; // OAuth2를 이용한 로그인 시 조회할 수 있는 Id, 카카오는 KAKAO_123984 네이버는 NAVER_s1e4jds와 같은 형태로 되어있음

    @NotBlank private String memberName;

    @Email private String email;

    @NotBlank private String password;

    @Pattern(regexp = "^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$")
    private String phoneNum;

    private String job;

    @NotBlank private String introduction; // 자기소개

    private boolean locationEnabled; // 위치 동의 여부

    private DetailsSignupRequest details;

    private LocalDate birth;

    private Double latitude; // 위치는 클라이언트 측에서 위도, 경도를 따로 전달받고 데이터베이스 저장 시 Point로 저장.

    private Double longitude;

    private String imgUrl;
    @Getter
    public static class DetailsSignupRequest {
        private String relationType;
        private boolean isMarried;
        private boolean hasChildren;
        private String personality;
        private String hobby;
    }

}
