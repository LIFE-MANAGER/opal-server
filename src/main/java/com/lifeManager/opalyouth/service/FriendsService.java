package com.lifeManager.opalyouth.service;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.common.response.BaseResponseStatus;
import com.lifeManager.opalyouth.dto.friends.BriefFriendsInfoResponse;
import com.lifeManager.opalyouth.dto.friends.DetailFriendsInfoResponse;
import com.lifeManager.opalyouth.dto.member.request.FriendsConditionRequest;
import com.lifeManager.opalyouth.entity.Details;
import com.lifeManager.opalyouth.entity.Location;
import com.lifeManager.opalyouth.entity.Member;
import com.lifeManager.opalyouth.entity.TodaysFriends;
import com.lifeManager.opalyouth.repository.DetailsRepository;
import com.lifeManager.opalyouth.repository.LocationRepository;
import com.lifeManager.opalyouth.repository.MemberRepository;
import com.lifeManager.opalyouth.repository.TodaysFriendsRepository;
import com.lifeManager.opalyouth.utils.RefreshRecommendUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.lifeManager.opalyouth.common.response.BaseResponseStatus.INIT_TODAY_FRIENDS_ERROR;
import static com.lifeManager.opalyouth.common.response.BaseResponseStatus.NON_EXIST_USER;

@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class FriendsService {

    private final MemberRepository memberRepository;
    private final TodaysFriendsRepository todaysFriendsRepository;
    private final RefreshRecommendUtils refreshRecommendUtils;
    private final DetailsRepository detailsRepository;

    private final LocationRepository locationRepository;

    public void refreshFriends(Principal principal) {
        Member member = memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NON_EXIST_USER));

        Optional<TodaysFriends> optionalTodaysFriends = todaysFriendsRepository.findByMember(member);

        TodaysFriends todaysFriends;
        if (optionalTodaysFriends.isEmpty()) {
            try {
                initTodaysFriends(member);
            } catch (Exception e) {
                todaysFriends = new TodaysFriends(member);
                todaysFriendsRepository.save(todaysFriends);
                throw new BaseException(INIT_TODAY_FRIENDS_ERROR);
            }

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
            try {
                todaysFriends = initTodaysFriends(member);
            } catch (Exception e) {
                throw new BaseException(INIT_TODAY_FRIENDS_ERROR);
            }
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


    private TodaysFriends initTodaysFriends(Member member) throws Exception {
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

    // 내 취향 반영 친구 추천
    public List<BriefFriendsInfoResponse> recommendByRelationType(Principal principal) throws BaseException {
        Member member = memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE)
                .orElseThrow(()-> new BaseException(NON_EXIST_USER));

        String relationType = member.getDetails().getRelationType();

        List<Details> byRelationType = detailsRepository.findByRelationType(relationType);
        byRelationType.removeIf(details -> details.getMember().equals(member));
        Collections.shuffle(byRelationType);    // 랜덤

        List<BriefFriendsInfoResponse> recommendFriendsResponseList = byRelationType.stream()
                .map(BriefFriendsInfoResponse::entityToBriefFriendInfoDto)
                .collect(Collectors.toList());


        if (recommendFriendsResponseList.isEmpty()) {
            return Collections.emptyList();
        }
        return recommendFriendsResponseList;
    }

    public List<BriefFriendsInfoResponse> recommendByDistance(Principal principal, int distance) {
        Member member = memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE)
                .orElseThrow(()-> new BaseException(NON_EXIST_USER));
        Point point = member.getLocation().getPoint();

        List<Location> getLocationByDistance = locationRepository.findLocationsWithinDistance(point, distance);
        getLocationByDistance.removeIf(location -> location.getMember().equals(member));
        Collections.shuffle(getLocationByDistance);

        List<BriefFriendsInfoResponse> recommendFriendsResponseList = getLocationByDistance
                .stream()
                .map(location -> BriefFriendsInfoResponse.entityToBriefFriendInfoDto(location.getMember()))
                .collect(Collectors.toList());

        if (recommendFriendsResponseList.isEmpty()) {
            return Collections.emptyList();
        }
        return recommendFriendsResponseList;
    }


    // 직접 찾기
    public List<BriefFriendsInfoResponse> recommendByCondition(Principal principal, FriendsConditionRequest friendsConditionRequest) throws BaseException {
        Member member = memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE)
                .orElseThrow(()-> new BaseException(NON_EXIST_USER));

        List<Member> memberList = memberRepository.findAll();
        List<Member> targetMembers = memberList.stream()
                .filter(target -> target.getGender().equals(friendsConditionRequest.getGender()))
                .filter(target -> {
                    int age = LocalDate.now().getYear() - target.getBirth().getBirth().getYear();
                    return age >= friendsConditionRequest.getAge() && age < friendsConditionRequest.getAge() + 10;
                })
                .filter(target -> target.getDetails().getMaritalStatus() == Details.stringToMaritalStatus(friendsConditionRequest.getMaritalStatus()))
                .filter(target -> target.getDetails().isHasChildren() == friendsConditionRequest.isHasChildren())
                .filter(target -> target.getDetails().getPersonality().equals(friendsConditionRequest.getPersonality()))
                .filter(target -> Location.getDistance(member.getLocation().getPoint() , target.getLocation().getPoint()) < friendsConditionRequest.getDistance())
                .collect(Collectors.toList());

        List<BriefFriendsInfoResponse> briefFriendsInfoResponseList = targetMembers.stream()
                .map(BriefFriendsInfoResponse::entityToBriefFriendInfoDto)
                .collect(Collectors.toList());

        if (briefFriendsInfoResponseList.isEmpty()) {
            return Collections.emptyList();
        }
        return briefFriendsInfoResponseList;
    }
}
