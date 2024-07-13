package com.lifeManager.opalyouth.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lifeManager.opalyouth.common.entity.BaseEntity;
import com.nimbusds.jose.shaded.json.annotate.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "job", length = 25)
    private String job;

    @Column(name = "introduction", nullable = false)
    private String introduction;

    // 위치 서비스 동의 todo : 네이티브 앱이 아닌 웹 서비스로 변경됨이 따라 위치 서비스 동의 필요가 없어짐, 개인정보동의 여부로 고치는 것 검토
    @Column(name = "location_YN", nullable = false)
    private boolean locationEnabled;

    // 구독 여부
    @Column(name = "subscribe", nullable = false)
    private boolean subscriptionStatus;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    // 닉네임 업데이트 날짜
    @Column
    private LocalDate nicknameUpdateAt;



    // 차단한 친구 목록
    @JsonBackReference
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Block> blockList = new ArrayList<Block>();

    // 채팅방 관련 정보 목록.
    @JsonBackReference
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<ChatroomMember> chatroomMemberList = new ArrayList<ChatroomMember>();

    // 친구 목록
    @JsonBackReference
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Friends> friendsList = new ArrayList<Friends>();

    // 프로필 사진 목록
    @Setter
    @JsonBackReference
    @OneToOne(mappedBy = "member")
    private Image image;

    // 친구 요청받은 목록
    @JsonBackReference
    @OneToMany(mappedBy = "requestedMember", cascade = CascadeType.REMOVE)
    private List<FriendRequest> friendRequestList = new ArrayList<FriendRequest>();

    // 호감표시한 멤버 목록
    @JsonBackReference
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Like> likeList = new ArrayList<Like>();

    @JsonBackReference
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "group_chat_idx")
    private GroupChat groupChat;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_details_idx")
    private Details details;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "member_birth_idx")
    private Birth birth;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JsonBackReference
    @JoinColumn(name = "member_location_idx")
    private Location location;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "member_item_idx")
    private item item;

    @Builder
    public Member(String idWithProvider, String nickname, String memberName, String email, String password, String phoneNum, String gender, String job, String introduction, boolean locationEnabled, boolean subscriptionStatus, Details details, Birth birth, Location location, com.lifeManager.opalyouth.entity.item item, LocalDate nicknameUpdateAt) {
        this.idWithProvider = idWithProvider;
        this.nickname = nickname;
        this.memberName = memberName;
        this.email = email;
        this.password = password;
        this.phoneNum = phoneNum;
        this.gender = gender;
        this.job = job;
        this.introduction = introduction;
        this.locationEnabled = locationEnabled;
        this.subscriptionStatus = subscriptionStatus;
        this.details = details;
        this.birth = birth;
        this.location = location;
        this.item = item;
        this.nicknameUpdateAt = nicknameUpdateAt;
    }

    public enum Role {
        USER,
        ADMIN
    }

    public void addBlock(Block block) {
        this.blockList.add(block);
        if (block.getBlockedMember() != this){
            block.setBlockedMember(this);
        }
    }

    public void addFriends(Friends friend) {
        this.friendsList.add(friend);
    }

    public void addFriendRequest(Member member) {
        FriendRequest friendRequest = new FriendRequest(this, member);
        this.friendRequestList.add(friendRequest);
    }

    public void addLikedMember(Like like) {
        this.likeList.add(like);
    }

    public void setInActive(){
        this.state = State.INACTIVE;
    }

    public void setActive(){
        this.state = State.ACTIVE;
    }
}
