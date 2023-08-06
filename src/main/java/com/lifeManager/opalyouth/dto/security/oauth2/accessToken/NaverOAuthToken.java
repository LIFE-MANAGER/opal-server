package com.lifeManager.opalyouth.dto.security.oauth2.accessToken;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NaverOAuthToken {
    /**
     * {
     * "access_token":"AAAAN9Yd8M1Vc9hyqXtObmK7Yt-4mBQpQ5UjecYwX6kOr8TKSNuA9FJWMowMC5TkFDqaqWkz91W_2dqGcd7A9RRx0l0",
     * "refresh_token":"N3Erqv8ODOSUS3N49ii9I41ePNIEKznsX2lm4PxLq9vHcXrZXVre5pVis2iszSSSyryhoMKL6nCtqdcO21yNBGeBEcipU1qV9DLyiitfvteDCJ1U9eQU0q1Pb497wy5GfSN68",
     * "token_type":"bearer",
     * "expires_in":"3600"
     * }
     */
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private int expiresIn;
}
