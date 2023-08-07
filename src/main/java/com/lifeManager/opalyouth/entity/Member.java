package com.lifeManager.opalyouth.entity;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
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

    @Column(nullable = false)
    private String idWithProvider; // OAuth2를 이용한 로그인 시 조회할 수 있는 Id, 카카오는 KAKAO_123984 네이버는 NAVER_s1e4jds와 같은 형태로 되어있음

    @Column(name = "nickname", nullable = false, length = 25)
    private String nickname; // 회원의 닉네임

    @Column(nullable = false, length = 50)
    private String memberName; // 회원의 실명

    @Column(name = "email", nullable = false, length = 50)
    private String email; // 회원의 이메일

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phoneNum", nullable = false)
    private String phoneNum;

    @Column(name = "job", length = 25)
    private String job;

    @Column(name = "introduction", nullable = false)
    private String introduction;

    // 위치 서비스 동의
    // 위치 서비스 동의 todo : 네이티브 앱이 아닌 웹 서비스로 변경됨이 따라 위치 서비스 동의 필요가 없어짐, 개인정보동의 여부로 고치는 것 검토
    @Column(name = "location_YN", nullable = false)
    private boolean locationEnabled;

    // 구독 여부
    @Column(name = "subscribe", nullable = false)
    private boolean subscriptionStatus;

    // 닉네임 업데이트 날짜
    @Column
    private LocalDate nicknameUpdateAt;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;


    // 차단한 친구 목록
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Block> blockList = new ArrayList<Block>();

    // 채팅방 관련 정보 목록.
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<ChatroomMember> chatroomMemberList = new ArrayList<ChatroomMember>();

    // 친구 목록
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Friends> friendsList = new ArrayList<Friends>();

    // 프로필 사진 목록
    @OneToMany
    @JoinColumn(name = "image_idx")
    private List<Image> imageList =  new ArrayList<Image>();

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "member_details_idx")
    private Details details;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "member_birth_idx")
    private Birth birth;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "member_location_idx")
    private Location location;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "member_item_idx")
    private item item;

    @Builder
    public Member(String idWithProvider, String nickname, String memberName, String email, String password, String phoneNum, String job, String introduction, boolean locationEnabled, boolean subscriptionStatus, Details details, Birth birth, Location location, com.lifeManager.opalyouth.entity.item item) {
        this.idWithProvider = idWithProvider;
        this.nickname = nickname;
        this.memberName = memberName;
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

    public enum Role {
        USER,
        ADMIN
    }
}
