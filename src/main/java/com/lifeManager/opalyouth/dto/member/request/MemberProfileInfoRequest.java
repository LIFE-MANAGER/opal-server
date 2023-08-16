package com.lifeManager.opalyouth.dto.member.request;

import com.lifeManager.opalyouth.entity.Details;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberProfileInfoRequest {
    private String job;
    private String introduction;
    private String maritalStatus;
    private boolean hasChildren;
    private String personality;
    private String hobby;
}
