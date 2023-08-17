package com.lifeManager.opalyouth.dto.member.request;

import com.lifeManager.opalyouth.entity.Details;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class FriendsConditionRequest {
    private String gender;
    private int age;
    private String maritalStatus;
    private boolean hasChildren;
    private String personality;
    private int distance;
}
