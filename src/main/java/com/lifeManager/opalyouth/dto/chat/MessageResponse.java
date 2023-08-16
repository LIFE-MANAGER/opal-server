package com.lifeManager.opalyouth.dto.chat;

import com.lifeManager.opalyouth.entity.Message;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MessageResponse {
    private String sender;
    private String contents;
    private LocalDateTime sendAt;

    public MessageResponse() {
    }

    public MessageResponse(String sender, String contents, LocalDateTime sendAt) {
        this.sender = sender;
        this.contents = contents;
        this.sendAt = sendAt;
    }

    public static MessageResponse entityToDto(Message message) {
        return new MessageResponse(
                message.getSendMember().getNickname(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}
