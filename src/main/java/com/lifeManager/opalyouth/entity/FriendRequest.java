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
    @JoinColumn(name = "requested_member_idx", nullable = false)
    private Member requestedMember;      // 요청 받은 사람 (나)

    @ManyToOne
    @JoinColumn(name = "request_member_idx", nullable = false)
    private Member requestMember;     // 요청한 사람

    @Builder
    public FriendRequest(Member requestedMember, Member requestMember) {
        this.requestedMember = requestedMember;
        this.requestMember = requestMember;
    }
}
