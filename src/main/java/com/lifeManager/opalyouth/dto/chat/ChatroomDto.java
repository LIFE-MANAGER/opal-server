package com.lifeManager.opalyouth.dto.chat;

import com.lifeManager.opalyouth.entity.Chatroom;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ChatroomDto {
    private String roomId;

    @Builder
    public ChatroomDto(String roomId) {
        this.roomId = roomId;
    }

    public ChatroomDto() {
    }

    public static ChatroomDto create() {
        ChatroomDto chatRoom = new ChatroomDto();
        chatRoom.roomId = UUID.randomUUID().toString();
        return chatRoom;
    }

    public static Chatroom createChatroomEntity(ChatroomDto chatRoomDto) {
        return new Chatroom(chatRoomDto.getRoomId());
    }

    public static ChatroomDto chatroomEntityToDto(Chatroom chatroom) {
        return new ChatroomDto(chatroom.getRoomId());
    }
}
