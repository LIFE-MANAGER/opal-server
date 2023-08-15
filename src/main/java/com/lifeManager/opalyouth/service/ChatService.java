package com.lifeManager.opalyouth.service;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.common.response.BaseResponseStatus;
import com.lifeManager.opalyouth.dto.chat.ChatMessageDto;
import com.lifeManager.opalyouth.dto.chat.ChatroomDto;
import com.lifeManager.opalyouth.dto.chat.ChatroomResponse;
import com.lifeManager.opalyouth.dto.chat.MessageResponse;
import com.lifeManager.opalyouth.entity.Chatroom;
import com.lifeManager.opalyouth.entity.ChatroomMember;
import com.lifeManager.opalyouth.entity.Member;
import com.lifeManager.opalyouth.entity.Message;
import com.lifeManager.opalyouth.repository.ChatroomMemberRepository;
import com.lifeManager.opalyouth.repository.ChatroomRepository;
import com.lifeManager.opalyouth.repository.MemberRepository;
import com.lifeManager.opalyouth.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChatroomRepository chatroomRepository;
    private final ChatroomMemberRepository chatroomMemberRepository;
    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;

    public ChatroomDto createRoom(Principal principal, String oppositeNickname) {
        Member member = memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NON_EXIST_USER));
        Member oppositeMember = memberRepository.findByNicknameAndState(oppositeNickname, BaseEntity.State.ACTIVE)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NON_EXIST_USER));

        for (ChatroomMember chatroomMember : member.getChatroomMemberList()) {
            String roomId = chatroomMember.getChatroom().getRoomId();
            for (ChatroomMember oppoChatroomMember : oppositeMember.getChatroomMemberList()) {
                String oppoRoomId = oppoChatroomMember.getChatroom().getRoomId();

                if (roomId.equals(oppoRoomId)) {
                    throw new BaseException(BaseResponseStatus.EXIST_CHAT_ROOM);
                }
            }
        }

        ChatroomDto chatRoomDto = ChatroomDto.create();
        Chatroom chatroomEntity = ChatroomDto.createChatroomEntity(chatRoomDto);

        try {
            Chatroom savedChatroom = chatroomRepository.save(chatroomEntity);

            chatroomMemberRepository.save(new ChatroomMember(member, savedChatroom));
            chatroomMemberRepository.save(new ChatroomMember(oppositeMember, savedChatroom));
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_INSERT_ERROR);
        }
        return chatRoomDto;
    }

    public List<ChatroomResponse> myChatroomList(Principal principal) {
        Member member = memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NON_EXIST_USER));

        List<ChatroomMember> chatroomMemberList = member.getChatroomMemberList();

        List<Chatroom> chatroomList = chatroomMemberList.stream().map(ChatroomMember::getChatroom).collect(Collectors.toList());

        return chatroomList.stream().map(ChatroomResponse::entityToDto).collect(Collectors.toList());
    }

    public void saveMessage(ChatMessageDto chatMessageDto) {
        Member member = memberRepository.findByNicknameAndState(chatMessageDto.getSender(), BaseEntity.State.ACTIVE)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NON_EXIST_USER));

        List<ChatroomMember> chatroomMemberList = member.getChatroomMemberList();

        for (ChatroomMember chatroomMember : chatroomMemberList) {
            if (chatroomMember.getChatroom().getRoomId().equals(chatMessageDto.getRoomId())) {
                Message message = ChatMessageDto.dtoToEntity(chatMessageDto, member, chatroomMember.getChatroom());
                messageRepository.save(message);
                break;
            }
        }
    }

    public List<MessageResponse> getBeforeChatMessage(Principal principal, String roomId) {

        Chatroom chatroom = chatroomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NON_EXIST_CHAT_ROOM));

        List<Message> messageList = messageRepository.findByChatroomOrderByCreatedAt(chatroom);

        return messageList.stream().map(MessageResponse::entityToDto).collect(Collectors.toList());
    }
}
