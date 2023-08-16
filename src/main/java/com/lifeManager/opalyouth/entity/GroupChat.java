package com.lifeManager.opalyouth.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table
public class GroupChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "groupChat")
    @JoinColumn(name = "member_idx")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "room_idx")
    private Chatroom chatroom;

    @Builder
    public GroupChat(Member member, Chatroom chatroom) {
        this.member = member;
        this.chatroom = chatroom;
    }

    public void updateGroupChatroom(Chatroom chatroom) {
        this.chatroom = chatroom;
    }
}
