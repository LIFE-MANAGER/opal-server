package com.lifeManager.opalyouth.service;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.common.response.BaseResponseStatus;
import com.lifeManager.opalyouth.dto.friends.BriefFriendsInfoResponse;
import com.lifeManager.opalyouth.dto.friends.DetailFriendsInfoResponse;
import com.lifeManager.opalyouth.entity.Member;
import com.lifeManager.opalyouth.entity.TodaysFriends;
import com.lifeManager.opalyouth.repository.MemberRepository;
import com.lifeManager.opalyouth.repository.TodaysFriendsRepository;
import com.lifeManager.opalyouth.utils.RefreshRecommendUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class FriendsService {

    private final MemberRepository memberRepository;
    private final TodaysFriendsRepository todaysFriendsRepository;
    private final RefreshRecommendUtils refreshRecommendUtils;

    public void refreshFriends(Principal principal) {
        Member member = memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NON_EXIST_USER));

        Optional<TodaysFriends> optionalTodaysFriends = todaysFriendsRepository.findByMember(member);

        TodaysFriends todaysFriends;
        if (optionalTodaysFriends.isEmpty()) {
            initTodaysFriends(member);

        } else {
            todaysFriends = optionalTodaysFriends.get();

            Member recommendByPersonality = refreshRecommendUtils.createRecommendByPersonality(todaysFriends.getMember().getDetails().getPersonality());
            while (Objects.equals(recommendByPersonality.getId(), todaysFriends.getId())) {
                recommendByPersonality = refreshRecommendUtils.createRecommendByPersonality(todaysFriends.getMember().getDetails().getPersonality());
            }


            Member recommendByRelationType = refreshRecommendUtils.createRecommendByRelationType(todaysFriends.getMember().getDetails().getRelationType());
            while (
                    Objects.equals(recommendByRelationType.getId(), todaysFriends.getId())
                            || Objects.equals(recommendByRelationType.getId(), recommendByPersonality.getId())
            ) {
                recommendByRelationType = refreshRecommendUtils.createRecommendByRelationType(todaysFriends.getMember().getDetails().getRelationType());
            }


            Member recommendByHobby = refreshRecommendUtils.createRecommendByHobby(todaysFriends.getMember().getDetails().getHobby());
            while (
                    Objects.equals(recommendByHobby.getId(), todaysFriends.getId())
                            || Objects.equals(recommendByHobby.getId(), recommendByRelationType.getId())
                            || Objects.equals(recommendByHobby.getId(), recommendByPersonality.getId())
            ) {
                recommendByHobby = refreshRecommendUtils.createRecommendByHobby(todaysFriends.getMember().getDetails().getHobby());
            }

            todaysFriends.updateRecommends(
                    recommendByPersonality,
                    recommendByRelationType,
                    recommendByHobby
            );
        }
    }

    @Transactional(readOnly = true)
    public List<BriefFriendsInfoResponse> getTodayFriends(Principal principal) {
        Member member = memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NON_EXIST_USER));
        Optional<TodaysFriends> optionalTodaysFriends = todaysFriendsRepository.findByMember(member);

        TodaysFriends todaysFriends;
        if (optionalTodaysFriends.isEmpty()) {
            todaysFriends = initTodaysFriends(member);
        } else todaysFriends = optionalTodaysFriends.get();

        Member memberByPersonality = todaysFriends.getMemberByPersonality();
        Member memberByRelationType = todaysFriends.getMemberByRelationType();
        Member memberByHobby = todaysFriends.getMemberByHobby();

        List<BriefFriendsInfoResponse> briefFriendsInfoResponseList = new ArrayList<>();
        briefFriendsInfoResponseList.add(BriefFriendsInfoResponse.entityToBriefFriendInfoDto(memberByPersonality));
        briefFriendsInfoResponseList.add(BriefFriendsInfoResponse.entityToBriefFriendInfoDto(memberByRelationType));
        briefFriendsInfoResponseList.add(BriefFriendsInfoResponse.entityToBriefFriendInfoDto(memberByHobby));

        return briefFriendsInfoResponseList;
    }

    @Transactional(readOnly = true)
    public DetailFriendsInfoResponse getMemberDetails(String nickname) {
        Member member = memberRepository.findByNicknameAndState(nickname, BaseEntity.State.ACTIVE)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NON_EXIST_USER));

        return DetailFriendsInfoResponse.entityToDetailFriendInfoDto(member);
    }


    private TodaysFriends initTodaysFriends(Member member) {
        TodaysFriends todaysFriends;
        Member recommendByPersonality = refreshRecommendUtils.createRecommendByPersonality(member.getDetails().getPersonality());

        Member recommendByRelationType = refreshRecommendUtils.createRecommendByRelationType(member.getDetails().getRelationType());
        while (
                Objects.equals(recommendByRelationType.getId(), recommendByPersonality.getId())
        ) {
            recommendByRelationType = refreshRecommendUtils.createRecommendByRelationType(member.getDetails().getRelationType());
        }


        Member recommendByHobby = refreshRecommendUtils.createRecommendByHobby(member.getDetails().getHobby());
        while (
                Objects.equals(recommendByHobby.getId(), recommendByRelationType.getId())
                        || Objects.equals(recommendByHobby.getId(), recommendByPersonality.getId())
        ) {
            recommendByHobby = refreshRecommendUtils.createRecommendByHobby(member.getDetails().getHobby());
        }

        todaysFriends = new TodaysFriends(member, recommendByPersonality, recommendByRelationType, recommendByHobby);

        try {
            return todaysFriendsRepository.save(todaysFriends);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_INSERT_ERROR);
        }
    }
}