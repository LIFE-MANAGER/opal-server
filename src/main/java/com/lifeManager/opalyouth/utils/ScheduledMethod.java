package com.lifeManager.opalyouth.utils;

import com.lifeManager.opalyouth.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScheduledMethod {

    private final RefreshRecommendUtils refreshRecommendUtils;
    private final ChatService chatService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduledExecute() {
        try {
            refreshRecommendUtils.execute();
        } catch (Exception e) {
            log.error("FAILED TO REFRESH TODAY'S RECOMMEND");
            e.printStackTrace();
        }
        try {
            chatService.createGroupChatroom();
        } catch (Exception e) {
            log.error("FAILED TO CREATE TODAY'S GROUP CHAT");
            e.printStackTrace();
        }
    }
}
