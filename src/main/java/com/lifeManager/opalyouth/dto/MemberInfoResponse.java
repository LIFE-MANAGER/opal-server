package com.lifeManager.opalyouth.dto;

import com.lifeManager.opalyouth.entity.Image;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class MemberInfoResponse {
    private List<Image> imageList;
    private LocalDate birth;
    private String nickname;
    private String job;
    private boolean isMarried;
    private boolean hasChildren;
    private String personality;
    private String hobby;
    private String introduction;    // 자기소개
}
