package com.lifeManager.opalyouth.dto.member;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class MemberInfoResponse {
    private String imageUrl;
    private LocalDate birth;
    private String nickname;
    private String job;
    private boolean isMarried;
    private boolean hasChildren;
    private String personality;
    private String hobby;
    private String introduction;    // 자기소개
    private Double latitude;
    private Double longitude;
    private int blockMemberNumber;
}
