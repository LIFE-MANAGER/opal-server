package com.lifeManager.opalyouth.dto.friends;

import com.lifeManager.opalyouth.entity.Details;
import com.lifeManager.opalyouth.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetailFriendsInfoResponse {
    private String nickname;
    private String job;
    private String maritalStatus;
    private boolean hasChildren;
    private String personality;
    private String hobby;
    private String relationType;
    private String introduction;    // 자기소개
    private Double latitude;
    private Double longitude;

    public DetailFriendsInfoResponse() {
    }

    public DetailFriendsInfoResponse(String nickname, String job, String maritalStatus, boolean hasChildren, String personality, String hobby, String relationType, String introduction, Double latitude, Double longitude) {
        this.nickname = nickname;
        this.job = job;
        this.maritalStatus = maritalStatus;
        this.hasChildren = hasChildren;
        this.personality = personality;
        this.hobby = hobby;
        this.relationType = relationType;
        this.introduction = introduction;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static DetailFriendsInfoResponse entityToDetailFriendInfoDto(Member member) {
        return new DetailFriendsInfoResponse(
                member.getNickname(),
                member.getJob(),
                member.getDetails().getMaritalStatus().toString(),
                member.getDetails().isHasChildren(),
                member.getDetails().getPersonality(),
                member.getDetails().getHobby(),
                member.getDetails().getRelationType(),
                member.getIntroduction(),
                member.getLocation().getLatitude(),
                member.getLocation().getLongitude()
        );
    }
}
