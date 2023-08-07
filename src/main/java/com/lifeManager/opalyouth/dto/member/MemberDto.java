package com.lifeManager.opalyouth.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MemberDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class ReqNickname {
        private String nickname;
    }
}
