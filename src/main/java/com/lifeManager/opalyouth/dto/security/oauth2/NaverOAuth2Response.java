package com.lifeManager.opalyouth.dto.security.oauth2;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NaverOAuth2Response {
    /**
     * {
     * "resultcode":"00",
     * "message":"success",
     * "response":
     * {
     * "id":"2A83SewhY9sEpSInnKktGLHk5eA23EFbzVjhvQSSYa4",
     * "name":"\uc774\ub3d9\ucc2c"
     * }
     * }
     */
    private String resultcode;
    private String message;
    private NaverResponse response;

    @Getter
    public static class NaverResponse {
        private String id;
        private String name;
    }
}
