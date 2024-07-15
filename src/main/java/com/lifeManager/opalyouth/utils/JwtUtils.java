package com.lifeManager.opalyouth.utils;

import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.common.response.BaseResponseStatus;
import com.lifeManager.opalyouth.entity.Member;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.lifeManager.opalyouth.common.properties.JwtProperties.ACCESS_TOKEN_EXPIRE_TIME;
import static com.lifeManager.opalyouth.common.properties.JwtProperties.REFRESH_TOKEN_EXPIRE_TIME;

/**
 * Jwt 와 관련된 유틸
 * - generateToken : 유저의 이메일 입력 시 accessToken, refreshToken값을 Map형태로 반환
 * - getUserEmail : jwt accessToken으로부터 user의 Email추출.
 */
@Component
public class JwtUtils {

    private final Key key;

    public JwtUtils(@Value("${jwt.secret}") String jwtSecret) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public Map<String, String> generateToken (Member member) {
        String accessToken = Jwts.builder()
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")
                .claim("uemail", member.getEmail())
                .claim("uid", member.getId())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        Map<String, String> tokenInfo = new HashMap<>();
        tokenInfo.put("accessToken", accessToken);
        tokenInfo.put("refreshToken", refreshToken);

        return tokenInfo;
    }

    public String getUserEmail(String accessToken) throws BaseException{
        try {
            String email = Jwts
                    .parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .get("uemail", String.class);
            return email;
        } catch (ExpiredJwtException expiredJwt) {
            throw new BaseException(BaseResponseStatus.EXPIRED_JWT_TOKEN);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.INVALID_JWT_TOKEN);
        }
    }

    public Long getUserId(String accessToken) throws BaseException {
        try {
            Long id = Jwts
                    .parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .get("uid", Long.class);
            return id;
        } catch (ExpiredJwtException expiredJwt) {
            throw new BaseException(BaseResponseStatus.EXPIRED_JWT_TOKEN);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.INVALID_JWT_TOKEN);
        }
    }
}
