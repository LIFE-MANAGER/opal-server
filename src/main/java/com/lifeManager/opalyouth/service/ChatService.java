package com.lifeManager.opalyouth.service;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.common.response.BaseResponseStatus;
import com.lifeManager.opalyouth.dto.chat.ChatMessageDto;
import com.lifeManager.opalyouth.dto.chat.ChatroomDto;
import com.lifeManager.opalyouth.dto.chat.ChatroomResponse;
import com.lifeManager.opalyouth.dto.chat.MessageResponse;
import com.lifeManager.opalyouth.entity.*;
import com.lifeManager.opalyouth.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ChatService {
    private final ChatroomRepository chatroomRepository;
    private final ChatroomMemberRepository chatroomMemberRepository;
    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;
    private final GroupChatRepository groupChatRepository;

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

    public ChatroomResponse getTodayChatParticipant(Principal principal) {
        Member member = memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NON_EXIST_USER));

        List<ChatroomMember> chatroomMemberList = member.getChatroomMemberList();
        ChatroomMember chatroomParticipant = chatroomMemberList
                .stream()
                .filter(chatroomMember
                        -> chatroomMember.getChatroom().getType().equals(Chatroom.RoomType.GROUP))
                .findFirst()
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NON_EXIST_CHAT_ROOM));

        return ChatroomResponse.entityToDto(chatroomParticipant.getChatroom());
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

    public void createGroupChatroom() {
        List<Member> allMembers = memberRepository.findAllByState(BaseEntity.State.ACTIVE);
        log.info("SHUFFLE BEFORE : {}", allMembers);
        Collections.shuffle(allMembers, new Random(System.nanoTime()));
        log.info("SHUFFLE AFTER : {}", allMembers);
        List<List<Member>> memberGroups = splitGroup(allMembers);
        chatroomRepository.deleteByType(Chatroom.RoomType.GROUP);
        log.info("MemberGroups : {}", memberGroups);
        for (List<Member> memberGroup : memberGroups) {
            ChatroomDto chatRoomDto = ChatroomDto.create();
            Chatroom chatroom = Chatroom.createChatroomByDto(chatRoomDto, Chatroom.RoomType.GROUP);
            chatroomRepository.save(chatroom);
            log.info("CHAT ROOM : {}", chatroom.getRoomId());
            for (Member member : memberGroup) {
                if (member.getGroupChat() != null) {
                    member.getGroupChat().updateGroupChatroom(chatroom);
                } else member.setGroupChat(new GroupChat(member, chatroom));
                chatroomMemberRepository.save(new ChatroomMember(member, chatroom));
            }
        }
    }

    private List<List<Member>> splitGroup(List<Member> members) {
        List<List<Member>> groups = new ArrayList<>();
        int currentIndex = 0;
        while (currentIndex < members.size()) {
            // 만약 남은 회원이 10명 미만일 경우 (10 + N) 명 을 마지막 채팅방에 넣음
            int groupSize = (members.size() - currentIndex < 20) ? members.size() - currentIndex : 10;
            groups.add(members.subList(currentIndex, currentIndex + groupSize));
            currentIndex += groupSize;
        }
        return groups;
    }

    public ChatroomResponse getGroupChatMember(Principal principal) {
        Member member = memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NON_EXIST_USER));
        GroupChat groupChat = groupChatRepository.findByMember(member)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR));

        return ChatroomResponse.entityToDto(groupChat.getChatroom());
    }
}
