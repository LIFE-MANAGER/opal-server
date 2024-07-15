package com.lifeManager.opalyouth.utils;

import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.common.response.BaseResponseStatus;
import com.lifeManager.opalyouth.entity.Member;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
class JwtUtilsTest {
    private JwtUtils jwtUtils;
    private String jwtSecret;

    @BeforeEach
    void setUp() {
        jwtSecret = "testJwtSecrettestJwtSecrettestJwtSecrettestJwtSecrettestJwtSecrettestJwtSecrettestJwtSecret";
        jwtUtils = new JwtUtils(jwtSecret);
    }

    @Test
    public void givenMemberEntity_whenGenerateToken_thenReturnToken() {
        // given
        Member member = Member.builder().email("test_email").build();
        member.setId(1L);

        // when
        Map<String, String> tokens = jwtUtils.generateToken(member);

        // then
        assertThat(tokens).containsKeys("accessToken", "refreshToken");
        assertThat(tokens.get("accessToken")).isNotNull();
        assertThat(tokens.get("refreshToken")).isNotNull();
    }

    @Test
    public void givenValidAccessToken_thenShouldContainUserEmailAndId() {
        // given
        Member member = Member.builder().email("test_email").build();
        member.setId(1L);
        Map<String, String> tokens = jwtUtils.generateToken(member);
        String accessToken = tokens.get("accessToken");

        // when
        String extractedUserEmail = jwtUtils.getUserEmail(accessToken);
        Long extractedUserId = jwtUtils.getUserId(accessToken);

        // then
        assertThat(extractedUserEmail).isEqualTo(member.getEmail());
        assertThat(extractedUserId).isEqualTo(member.getId());
    }

    @Test
    public void givenExpiredAccessToken_whenGetUserEmail_thenThrowException() {
        // given
        Member member = Member.builder().email("test_email").build();
        member.setId(1L);
        String expiredAccessToken = Jwts.builder()
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")
                .claim("uemail", member.getEmail())
                .claim("uid", member.getId())
                .setExpiration(new Date(System.currentTimeMillis()))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)), SignatureAlgorithm.HS256)
                .compact();

        // when, then
        assertThatThrownBy(() -> jwtUtils.getUserEmail(expiredAccessToken))
                .isInstanceOf(BaseException.class)
                .hasMessage(BaseResponseStatus.EXPIRED_JWT_TOKEN.getMessage())
                .hasFieldOrPropertyWithValue("status", BaseResponseStatus.EXPIRED_JWT_TOKEN);
    }

    @Test
    public void givenInvalidAccessToken_whenGetUserEmail_thenThrowException() {
        // given
        Member member = Member.builder().email("test_email").build();
        member.setId(1L);
        String invalidJwtKey = "invalidJwtKeyinvalidJwtKeyinvalidJwtKeyinvalidJwtKeyinvalidJwtKeyinvalidJwtKeyinvalidJwtKey";
        String expiredAccessToken = Jwts.builder()
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")
                .claim("uemail", member.getEmail())
                .claim("uid", member.getId())
                .setExpiration(new Date(System.currentTimeMillis()))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(invalidJwtKey)), SignatureAlgorithm.HS256)
                .compact();

        // when, then
        assertThatThrownBy(() -> jwtUtils.getUserEmail(expiredAccessToken))
                .isInstanceOf(BaseException.class)
                .hasMessage(BaseResponseStatus.INVALID_JWT_TOKEN.getMessage())
                .hasFieldOrPropertyWithValue("status", BaseResponseStatus.INVALID_JWT_TOKEN);
    }

    @Test
    public void givenExpiredAccessToken_whenGetUserId_thenThrowException() {
        // given
        Member member = Member.builder().email("test_email").build();
        member.setId(1L);
        String expiredAccessToken = Jwts.builder()
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")
                .claim("uemail", member.getEmail())
                .claim("uid", member.getId())
                .setExpiration(new Date(System.currentTimeMillis()))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)), SignatureAlgorithm.HS256)
                .compact();

        // when, then
        assertThatThrownBy(() -> jwtUtils.getUserId(expiredAccessToken))
                .isInstanceOf(BaseException.class)
                .hasMessage(BaseResponseStatus.EXPIRED_JWT_TOKEN.getMessage())
                .hasFieldOrPropertyWithValue("status", BaseResponseStatus.EXPIRED_JWT_TOKEN);
    }

    @Test
    public void givenInvalidAccessToken_whenGetUserId_thenThrowException() {
        // given
        Member member = Member.builder().email("test_email").build();
        member.setId(1L);
        String invalidJwtKey = "invalidJwtKeyinvalidJwtKeyinvalidJwtKeyinvalidJwtKeyinvalidJwtKeyinvalidJwtKeyinvalidJwtKey";
        String expiredAccessToken = Jwts.builder()
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")
                .claim("uemail", member.getEmail())
                .claim("uid", member.getId())
                .setExpiration(new Date(System.currentTimeMillis()))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(invalidJwtKey)), SignatureAlgorithm.HS256)
                .compact();

        // when, then
        assertThatThrownBy(() -> jwtUtils.getUserId(expiredAccessToken))
                .isInstanceOf(BaseException.class)
                .hasMessage(BaseResponseStatus.INVALID_JWT_TOKEN.getMessage())
                .hasFieldOrPropertyWithValue("status", BaseResponseStatus.INVALID_JWT_TOKEN);
    }
}