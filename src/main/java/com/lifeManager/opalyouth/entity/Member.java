package com.lifeManager.opalyouth.entity;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Member")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_idx")
    private Long memberIdx;

    @Column(name = "nickname", nullable = false, length = 25)
    private String nickname;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phoneNum", nullable = false)
    private String phoneNum;

    @Column(name = "job", nullable = false, length = 25)
    private String job;

    @Column(name = "details", nullable = false)
    private String details;

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

    @Builder
    public Member(String nickname, String email, String password, String phoneNum, String job, String details, boolean locationEnabled, boolean subscriptionStatus) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.phoneNum = phoneNum;
        this.job = job;
        this.details = details;
        this.locationEnabled = locationEnabled;
        this.subscriptionStatus = subscriptionStatus;
    }
}
