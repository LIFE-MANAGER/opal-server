package com.lifeManager.opalyouth.dto.member;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberInfoResponse { // 마이페이지 등 Member 관련 응답 반환 시 사용
    private String nickname;
    private String job;
    private boolean isMarried;
    private boolean hasChildren;
    private String personality;
    private String hobby;
    private String introduction;    // 자기소개
}
