package com.lifeManager.opalyouth.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "friend_request")
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_idx", nullable = false)
    private Member member;      // 요청한 사람

    @ManyToOne
    @JoinColumn(name = "requested_member_idx", nullable = false)
    private Member requestedMember;     // 요청 받은 사람

    @Builder
    public FriendRequest(Member member, Member requestedMember) {
        this.member = member;
        this.requestedMember = requestedMember;
        requestedMember.getFriendRequestList().add(this);
    }
}
