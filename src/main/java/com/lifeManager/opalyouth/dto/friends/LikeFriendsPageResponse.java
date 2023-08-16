package com.lifeManager.opalyouth.dto.friends;

import com.lifeManager.opalyouth.entity.Like;
import com.lifeManager.opalyouth.entity.Location;
import org.locationtech.jts.geom.Point;
import lombok.Getter;

import java.awt.*;
import java.time.LocalDate;

@Getter
public class LikeFriendsPageResponse {
    private String imageUrl;
    private String nickname;
    private LocalDate birth;
    private Double latitude;
    private Double longitude;

    public LikeFriendsPageResponse() {
    }

    public LikeFriendsPageResponse(String imageUrl, String nickname, LocalDate birth, Double latitude, Double longitude) {
        this.imageUrl = imageUrl;
        this.nickname = nickname;
        this.birth = birth;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static LikeFriendsPageResponse LikeFriendEntityToLikeRes(Like like) {
        return new LikeFriendsPageResponse(
                like.getLikedMember().getImage().getUrl(),
                like.getLikedMember().getNickname(),
                like.getLikedMember().getBirth().getBirth(),
                like.getLikedMember().getLocation().getLatitude(),
                like.getLikedMember().getLocation().getLongitude()
        );
    }
}
