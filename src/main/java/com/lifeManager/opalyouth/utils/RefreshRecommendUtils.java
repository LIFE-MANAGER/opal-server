package com.lifeManager.opalyouth.utils;

import com.lifeManager.opalyouth.entity.Details;
import com.lifeManager.opalyouth.entity.Member;
import com.lifeManager.opalyouth.entity.TodaysFriends;
import com.lifeManager.opalyouth.repository.DetailsRepository;
import com.lifeManager.opalyouth.repository.MemberRepository;
import com.lifeManager.opalyouth.repository.TodaysFriendsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Component
public class RefreshRecommendUtils {

    private final TodaysFriendsRepository todaysFriendsRepository;
    private final DetailsRepository detailsRepository;

    public Member createRecommendByPersonality (String personality) {
        List<Details> byPersonality = detailsRepository.findByPersonality(personality);
        if (byPersonality.isEmpty()) {
            return null;
        } else {
            Random random = new Random(System.nanoTime());
            int idx = random.nextInt(byPersonality.size());
            return byPersonality.get(idx).getMember();
        }
    }

    public Member createRecommendByRelationType (String relationType) {
        List<Details> byRelationType = detailsRepository.findByRelationType(relationType);
        if (byRelationType.isEmpty()) {
            return null;
        } else {
            Random random = new Random(System.nanoTime());
            int idx = random.nextInt(byRelationType.size());
            return byRelationType.get(idx).getMember();
        }
    }

    public Member createRecommendByHobby (String hobby) {
        List<Details> byHobby = detailsRepository.findByHobby(hobby);
        if (byHobby.isEmpty()) {
            return null;
        } else {
            Random random = new Random(System.nanoTime());
            int idx = random.nextInt(byHobby.size());
            return byHobby.get(idx).getMember();
        }
    }


    public void execute() throws Exception {
        final List<TodaysFriends> todaysFriendsList = todaysFriendsRepository.findAll();

        for (TodaysFriends todaysFriends : todaysFriendsList) {
            try {
                updateTodayFriend(todaysFriends);
            } catch (Exception e) {
                log.error("오늘의 추천 새로고침 중 에러가 발생하였습니다. 새로고침 실패한 회원 Index : {}", todaysFriends.getMember().getId());
            }
        }
    }

    private void updateTodayFriend(TodaysFriends todaysFriends) throws Exception {
        Member recommendByPersonality = createRecommendByPersonality(todaysFriends.getMember().getDetails().getPersonality());
        int repeat = 0;
        while (Objects.equals(recommendByPersonality.getId(), todaysFriends.getId())) {
            recommendByPersonality = createRecommendByPersonality(todaysFriends.getMember().getDetails().getPersonality());

            repeat += 1;
            if (repeat > 20) {
                recommendByPersonality = null;
                break;
            }
        }


        Member recommendByRelationType = createRecommendByRelationType(todaysFriends.getMember().getDetails().getRelationType());
        repeat = 0;
        while (
                Objects.equals(recommendByRelationType.getId(), todaysFriends.getId())
                || Objects.equals(recommendByRelationType.getId(), recommendByPersonality.getId())
        ) {
            recommendByRelationType = createRecommendByRelationType(todaysFriends.getMember().getDetails().getRelationType());

            repeat += 1;
            if (repeat > 20) {
                recommendByPersonality = null;
                break;
            }
        }


        Member recommendByHobby = createRecommendByHobby(todaysFriends.getMember().getDetails().getHobby());
        repeat = 0;
        while (
                Objects.equals(recommendByHobby.getId(), todaysFriends.getId())
                        || Objects.equals(recommendByHobby.getId(), recommendByRelationType.getId())
                        || Objects.equals(recommendByHobby.getId(), recommendByPersonality.getId())
        ) {
            recommendByHobby = createRecommendByHobby(todaysFriends.getMember().getDetails().getHobby());

            repeat += 1;
            if (repeat > 20) {
                recommendByPersonality = null;
                break;
            }
        }

        todaysFriends.updateRecommends(
                recommendByPersonality,
                recommendByRelationType,
                recommendByHobby
        );
    }
}
