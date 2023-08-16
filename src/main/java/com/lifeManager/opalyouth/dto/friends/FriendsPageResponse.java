package com.lifeManager.opalyouth.dto.friends;

import com.lifeManager.opalyouth.entity.Birth;
import com.lifeManager.opalyouth.entity.Friends;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class FriendsPageResponse {
    // 내 프로필
    private String imageUrl;
    private String nickname;
    private String introduction;

    private List<FriendInfoResponse> friendInfoResponseList;

    public FriendsPageResponse(String imageUrl, String nickname, String introduction, List<FriendInfoResponse> friendInfoResponseList) {
        this.imageUrl = imageUrl;
        this.nickname = nickname;
        this.introduction = introduction;
        this.friendInfoResponseList = friendInfoResponseList;
    }

    @Getter
    public static class FriendInfoResponse {
        private String nickname;
        private LocalDate birth;
        private String introduction;

        public FriendInfoResponse() {
        }

        public FriendInfoResponse(String nickname, LocalDate birth, String introduction) {
            this.nickname = nickname;
            this.birth = birth;
            this.introduction = introduction;
        }

        public static FriendInfoResponse FriendEntityToFriendRes(Friends friends){
            return new FriendInfoResponse(
                    friends.getFriend().getNickname(),
                    friends.getFriend().getBirth().getBirth(),
                    friends.getFriend().getIntroduction()
            );
        }
    }
}
