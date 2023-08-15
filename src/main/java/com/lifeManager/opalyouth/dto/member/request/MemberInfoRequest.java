package com.lifeManager.opalyouth.dto.member.request;

import lombok.Getter;

@Getter
public class MemberInfoRequest {
    private String nickname;
    private String job;
    private String maritalStatus;
    private boolean hasChildren;
    private String personality;
    private String hobby;
    private String introduction;    // 자기소개
    private Double latitude;
    private Double longitude;
}
