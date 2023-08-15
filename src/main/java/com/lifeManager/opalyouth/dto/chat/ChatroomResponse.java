package com.lifeManager.opalyouth.dto.chat;

import com.lifeManager.opalyouth.entity.Chatroom;
import com.lifeManager.opalyouth.entity.ChatroomMember;
import com.lifeManager.opalyouth.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ChatroomResponse {
    private String roomId;
    private List<ParticipantInfo> participants;


    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    public static class ParticipantInfo {
        private Long memberId;
        private String nickname;

        public static ParticipantInfo entityToDto(Member member) {
            return new ParticipantInfo(member.getId(), member.getNickname());
        }
    }

    public ChatroomResponse() {
    }

    public ChatroomResponse(String roomId, List<ParticipantInfo> participants) {
        this.roomId = roomId;
        this.participants = participants;
    }

    public static ChatroomResponse entityToDto(Chatroom chatroom) {
        return new ChatroomResponse(
                chatroom.getRoomId(),
                chatroom.getChatroomMemberList().stream()
                        .map(chatroomMember -> ParticipantInfo.entityToDto(chatroomMember.getMember()))
                        .collect(Collectors.toList())
        );
    }
}
