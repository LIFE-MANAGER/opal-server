package com.lifeManager.opalyouth.filter;

import com.lifeManager.opalyouth.common.properties.JwtProperties;
import com.lifeManager.opalyouth.utils.JwtUtils;
import com.nimbusds.jwt.JWT;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class FilterChannelInterceptor implements ChannelInterceptor {

    private final JwtUtils jwtUtils;

    /**
     * Message 가 채널로 전송되기 전에 호출되는 메소드
     * MessageHeaderAccessor를 통하여 STOMP 헤더에 접근
     * @param message
     * @param channel
     * @return
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        assert headerAccessor != null;

        if (headerAccessor.getCommand() == StompCommand.CONNECT) { // 연결 시에한 header 확인
            String token = String.valueOf(headerAccessor.getNativeHeader("Authorization").get(0));
            token = token.replace(JwtProperties.JWT_ACCESS_TOKEN_TYPE, "");

            Long userId = jwtUtils.getUserId(token);
            headerAccessor.addNativeHeader("uid", String.valueOf(userId));
        }
        return message;
    }
}
