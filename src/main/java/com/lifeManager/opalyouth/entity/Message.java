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
@Table(name = "message")
public class Message extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "send_member_idx")
    private Member sendMember;

    @ManyToOne
    @JoinColumn(name = "chatroom_idx")
    private Chatroom chatroom;

    @Builder
    public Message(String content, Member sendMember, Chatroom chatroom) {
        this.content = content;
        this.sendMember = sendMember;
        this.chatroom = chatroom;
    }
}
