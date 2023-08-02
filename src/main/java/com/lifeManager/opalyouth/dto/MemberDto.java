package com.lifeManager.opalyouth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDto {
    private String nickname;
    private String job;
    private boolean isMarried;
    private boolean hasChildren;
    private String personality;
    private String hobby;
    private String introduction;    // 자기소개
}
