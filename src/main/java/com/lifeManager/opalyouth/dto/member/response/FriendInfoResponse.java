package com.lifeManager.opalyouth.dto.member.response;

import com.lifeManager.opalyouth.entity.Birth;
import com.lifeManager.opalyouth.entity.Friends;
import lombok.Getter;

@Getter
public class FriendInfoResponse {
    private String nickname;
    private Birth birth;
    private String introduction;

    public FriendInfoResponse() {
    }

    public FriendInfoResponse(String nickname, Birth birth, String introduction) {
        this.nickname = nickname;
        this.birth = birth;
        this.introduction = introduction;
    }

    public static FriendInfoResponse FriendEntityToFriendRes(Friends friends){
        return new FriendInfoResponse(
                friends.getFriend().getNickname(),
                friends.getFriend().getBirth(),
                friends.getFriend().getIntroduction()
        );
    }
}
