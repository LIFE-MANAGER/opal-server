package com.lifeManager.opalyouth.common.config;

import com.lifeManager.opalyouth.filter.FilterChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@RequiredArgsConstructor
@EnableWebSocketMessageBroker
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final FilterChannelInterceptor filterChannelInterceptor;

    // MessageBroker 구성
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub"); // 해당 주소를 구독하는 클라이언트에게 메시지를 보낸다. 클라이언트에서 1번 채널을 구독하고 싶을 때 /sub/1과 같이 앤드포인트를 설정한다.
        registry.setApplicationDestinationPrefixes("/pub"); // /pub으로 시작하는 메시지만 해당 Broker에서 받아 처리한다.
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트에서 웹소켓에 접속할 수 있는 앤드포인트를 설정한다. (예시 = ws://localhost:9000/stomp/chat)
        registry.addEndpoint("/stomp/chat")
                .setAllowedOriginPatterns("*") // 모든 오리진 허용. (cors 오류 방지를 위함 나중에 클라이언트의 origin만 허용 시켜야함.)
                .withSockJS();
    }
    // todo : 클라이언트 구현 완료 시 주석 해제
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(filterChannelInterceptor);
//    }
}
