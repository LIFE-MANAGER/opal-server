package com.lifeManager.opalyouth.controller;

import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.common.response.BaseResponse;
import com.lifeManager.opalyouth.dto.chat.ChatroomDto;
import com.lifeManager.opalyouth.dto.chat.ChatroomResponse;
import com.lifeManager.opalyouth.dto.chat.MessageResponse;
import com.lifeManager.opalyouth.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chat")
@RestController
public class ChatController {

    private final ChatService chatService;


    /**
     * 채팅방 생성
     * - 생성 시 roomId를 반환
     * 반환 할 수 있는 예외
     * - NON_EXIST_USER, EXIST_CHAT_ROOM, DATABASE_INSERT_ERROR
     *
     * @param principal
     * @param oppositeNickname
     * @return
     */
    @PostMapping("/room")
    public BaseResponse<ChatroomDto> createRoom(Principal principal, @RequestParam String oppositeNickname) {
        try {
            ChatroomDto chatroomDto = chatService.createRoom(principal, oppositeNickname);
            return new BaseResponse<>(chatroomDto);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 내 채팅방 리스트 반환
     * - roomId 와 participant 반환함 (참가자에는 나의 정보도 포함되어있음.)
     *
     * 발생할 수 있는 예외
     * - NON_EXIST_USER
     *
     * @param principal
     * @return
     */
    @GetMapping("/rooms")
    public BaseResponse<List<ChatroomResponse>> myChatroomList(Principal principal) {
        try {
            List<ChatroomResponse> chatroomDtoList = chatService.myChatroomList(principal);
            return new BaseResponse<>(chatroomDtoList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 전에 대화했던 채팅 메시지 반환.
     *
     * 발생할 수 있는 예외
     * - NON_EXIST_CHAT_ROOM
     * @param principal
     * @param roomId
     * @return
     */
    @GetMapping("/room/{roomId}")
    public BaseResponse<List<MessageResponse>> getBeforeChatMessage(Principal principal, @PathVariable String roomId) {
        try {
            List<MessageResponse> messageResponseList = chatService.getBeforeChatMessage(principal, roomId);
            return new BaseResponse<>(messageResponseList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 오늘의 채팅 맴버 반환하기
     * @param principal
     * @return
     */
    @GetMapping("/today")
    public BaseResponse<ChatroomResponse> getTodayChatParticipant(Principal principal) {
        try {
            ChatroomResponse todayChatParticipant = chatService.getTodayChatParticipant(principal);
            return new BaseResponse<>(todayChatParticipant);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
