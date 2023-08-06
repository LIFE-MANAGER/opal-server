package com.lifeManager.opalyouth.dto.security.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@SuppressWarnings("unchecked")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoOAuth2Response {
    private Long id;
    @JsonProperty("connected_at")
    private LocalDateTime connectedAt;
    private Map<String, Object> properties;
    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    public String getEmail() {
        return this.kakaoAccount.email;
    }

    public String getNickname() {
        return this.kakaoAccount.profile.nickname;
    }

    public static KakaoOAuth2Response buildKakaoOAuth2Response(Map<String, Object> attributes) {
        return new KakaoOAuth2Response(
                Long.valueOf(String.valueOf(attributes.get("id"))),
                LocalDateTime.parse(
                        String.valueOf(attributes.get("connected_at")),
                        DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault())
                ),
                (Map<String, Object>) attributes.get("properties"),
                KakaoAccount.buildKakaoAccount((Map<String, Object>) attributes.get("kakao_account"))
        );
    }
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class KakaoAccount {
        @JsonProperty("profile_nickname_needs_agreement")
        private Boolean profileNicknameNeedsAgreement;
        private Profile profile;
        private Boolean hasEmail;
        @JsonProperty("email_needs_agreement")
        private Boolean emailNeedsAgreement;
        @JsonProperty("is_email_valid")
        private Boolean isEmailValid;
        @JsonProperty("is_email_verified")
        private Boolean isEmailVerified;
        private String email;

        public static KakaoAccount buildKakaoAccount(Map<String, Object> attributes) {
            return new KakaoAccount(
                    Boolean.valueOf(String.valueOf(attributes.get("profile_nickname_needs_agreement"))),
                    Profile.from((Map<String, Object>) attributes.get("profile")),
                    Boolean.valueOf(String.valueOf(attributes.get("has_email"))),
                    Boolean.valueOf(String.valueOf(attributes.get("email_needs_agreement"))),
                    Boolean.valueOf(String.valueOf(attributes.get("is_email_valid"))),
                    Boolean.valueOf(String.valueOf(attributes.get("is_email_verified"))),
                    String.valueOf(attributes.get("email"))
            );
        }
    }

    @Setter
    @Getter
    public static class Profile {

        private String nickname;

        public Profile() { }
        public Profile(String nickname) {
            this.nickname = nickname;
        }

        public static Profile from(Map<String, Object> attributes) {
            return new Profile(String.valueOf(attributes.get("nickname")));
        }
    }
}
