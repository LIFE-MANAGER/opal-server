package com.lifeManager.opalyouth.dto.friends;

import com.lifeManager.opalyouth.entity.Details;
import com.lifeManager.opalyouth.entity.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BriefFriendsInfoResponse {
    private Long userId;
    private String imageUrl;
    private String nickname;
    private LocalDate birth;
    private Double latitude;
    private Double longitude;
//    private LocalDateTime recentAccessTime; // todo : 컬럼에 최근 접속 일시 추가
    private String personality;
    private String hobby;
    private String relationType;


    public BriefFriendsInfoResponse() {
    }

    public BriefFriendsInfoResponse(Long userId, String imageUrl, String nickname, LocalDate birth, Double latitude, Double longitude, String personality, String hobby, String relationType) {
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.nickname = nickname;
        this.birth = birth;
        this.latitude = latitude;
        this.longitude = longitude;
        this.personality = personality;
        this.hobby = hobby;
        this.relationType = relationType;
    }

    public static BriefFriendsInfoResponse entityToBriefFriendInfoDto(Member member) {
        return new BriefFriendsInfoResponse(
                member.getId(),
                member.getImage().getUrl(),
                member.getNickname(),
                member.getBirth().getBirth(),
                member.getLocation().getLatitude(),
                member.getLocation().getLongitude(),
                member.getDetails().getPersonality(),
                member.getDetails().getHobby(),
                member.getDetails().getRelationType()
        );
    }

    public static BriefFriendsInfoResponse entityToBriefFriendInfoDto(Details details) {
        return new BriefFriendsInfoResponse(
                details.getMember().getId(),
                details.getMember().getImage().getUrl(),
                details.getMember().getNickname(),
                details.getMember().getBirth().getBirth(),
                details.getMember().getLocation().getLatitude(),
                details.getMember().getLocation().getLongitude(),
                details.getMember().getDetails().getPersonality(),
                details.getMember().getDetails().getHobby(),
                details.getMember().getDetails().getRelationType()
        );
    }
}
