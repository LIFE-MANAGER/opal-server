package com.lifeManager.opalyouth.dto.chat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.lifeManager.opalyouth.entity.Chatroom;
import com.lifeManager.opalyouth.entity.Member;
import com.lifeManager.opalyouth.entity.Message;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDto {

    private MessageType type;
    private String roomId;
    private String sender;
    private String message;


    public static Message dtoToEntity(ChatMessageDto chatMessageDto, Member sendMember, Chatroom chatroom) {
        return new Message(
                chatMessageDto.getMessage(),
                sendMember,
                chatroom
        );
    }


    @JsonProperty("type")
    public MessageType getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(MessageType type) {
        this.type = type;
    }

    public enum MessageType {
        ENTER("ENTER"),
        TALK("TALK"),
        QUIT("QUIT")
        ;

        private final String value;

        MessageType(String value) {
            this.value = value;
        }

        @JsonCreator
        public static MessageType fromValue(String value) {
            for (MessageType messageType : MessageType.values()) {
                if (messageType.value.equalsIgnoreCase(value)) {
                    return messageType;
                }
            }
            throw new IllegalArgumentException("Invalid MessageType value: " + value);
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }
}
