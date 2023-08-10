package com.lifeManager.opalyouth.dto.member;

import lombok.Getter;

@Getter
public class MemberProfileInfoRequest {
    private String job;
    private String introduction;
    private boolean isMarried;
    private boolean hasChildren;
    private String personality;
    private String hobby;
}
