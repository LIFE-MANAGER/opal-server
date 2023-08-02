package com.lifeManager.opalyouth.entity;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Member")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", nullable = false, length = 25)
    private String nickname;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phoneNum", nullable = false)
    private String phoneNum;

    @Column(name = "job", length = 25)
    private String job;

    @Column(name = "introduction", nullable = false)
    private String introduction;

    // 위치 서비스 동의
    @Column(name = "location_YN", nullable = false)
    private boolean locationEnabled;

    // 구독 여부
    @Column(name = "subscribe", nullable = false)
    private boolean subscriptionStatus;

    // 차단한 친구 목록
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Block> blockList = new ArrayList<Block>();

    // 채팅방 관련 정보 목록.
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<ChatroomMember> chatroomMemberList = new ArrayList<ChatroomMember>();

    // 친구 목록
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Friends> friendsList = new ArrayList<Friends>();

    @OneToOne
    @JoinColumn(name = "member_details_idx")
    private Details details;

    @OneToOne
    @JoinColumn(name = "member_birth_idx")
    private Birth birth;

    @OneToOne
    @JoinColumn(name = "member_location_idx")
    private Location location;

    @OneToOne
    @JoinColumn(name = "member_item_idx")
    private item item;

    @Builder
    public Member(String nickname, String email, String password, String phoneNum, String job, String introduction, boolean locationEnabled, boolean subscriptionStatus, Details details, Birth birth, Location location, com.lifeManager.opalyouth.entity.item item) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.phoneNum = phoneNum;
        this.job = job;
        this.introduction = introduction;
        this.locationEnabled = locationEnabled;
        this.subscriptionStatus = subscriptionStatus;
        this.details = details;
        this.birth = birth;
        this.location = location;
        this.item = item;
    }
}
