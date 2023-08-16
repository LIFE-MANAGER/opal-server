package com.lifeManager.opalyouth.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lifeManager.opalyouth.common.entity.BaseEntity;
import com.lifeManager.opalyouth.dto.chat.ChatroomDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chatroom")
public class Chatroom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id", nullable = false)
    private String roomId;

    @Column(name = "type", nullable = false, length = 25)
    @Enumerated(value = EnumType.STRING)
    private RoomType type = RoomType.PRIVATE;

    @JsonBackReference
    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.ALL)
    private List<ChatroomMember> chatroomMemberList = new ArrayList<>();

    @Builder
    public Chatroom(String roomId) {
        this.roomId = roomId;
    }

    public Chatroom(String roomId, RoomType type) {
        this.roomId = roomId;
        this.type = type;
    }

    public static Chatroom createChatroomByDto(ChatroomDto chatroomDto, RoomType roomType) {
        return new Chatroom(
                chatroomDto.getRoomId(),
                roomType
        );
    }

    public enum RoomType {
        PRIVATE,
        GROUP
    }

}
