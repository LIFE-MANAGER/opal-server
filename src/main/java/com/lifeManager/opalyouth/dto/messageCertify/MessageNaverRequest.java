package com.lifeManager.opalyouth.dto.messageCertify;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MessageNaverRequest {
    private String type;
    private String contentType;
    private String countryCode;
    private String from;
    private String content;
    private List<MessagesDto> messages;

    @Builder
    public MessageNaverRequest(
            String type,
            String contentType,
            String countryCode,
            String from,
            String content,
            List<MessagesDto> messages
    ) {
        this.type = type;
        this.contentType = contentType;
        this.countryCode = countryCode;
        this.from = from;
        this.content = content;
        this.messages = messages;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MessagesDto {
        private String to;

        @Builder
        public MessagesDto(String to) {
            this.to = to;
        }
    }
}
