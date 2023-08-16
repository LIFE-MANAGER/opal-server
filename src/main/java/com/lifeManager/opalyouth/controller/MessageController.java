package com.lifeManager.opalyouth.controller;

import com.lifeManager.opalyouth.dto.chat.ChatMessageDto;
import com.lifeManager.opalyouth.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MessageController {
    private final SimpMessageSendingOperations messageSendingOperations;
    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void message(ChatMessageDto chatMessageDto) {
        if (ChatMessageDto.MessageType.ENTER.equals(chatMessageDto.getType())) {
            chatMessageDto.setMessage(chatMessageDto.getSender() + "님이 입장하였습니다.");
        }
        log.info("chat message : {}", chatMessageDto.getMessage());
        messageSendingOperations.convertAndSend("/sub/chat/room/" + chatMessageDto.getRoomId(), chatMessageDto);
        try {
            // todo : Header의 userId 뽑을 수 있는지 확인
            // Long userId = stompHeaderAccessor.getMessageHeaders().get("uid", Long.class);
            chatService.saveMessage(chatMessageDto);
        } catch (Exception e) {
            log.error("FAIL TO SAVE MESSAGE : {}", e.getMessage());
        }
    }
}
