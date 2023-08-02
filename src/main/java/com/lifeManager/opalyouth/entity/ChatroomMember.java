package com.lifeManager.opalyouth.entity;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "chatroom_member")
public class ChatroomMember extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_idx")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "chatroom_idx")
    private Chatroom chatroom;

    @Builder
    public ChatroomMember(Member member, Chatroom chatroom) {
        this.member = member;
        this.chatroom = chatroom;
        member.getChatroomMemberList().add(this);
    }
}
