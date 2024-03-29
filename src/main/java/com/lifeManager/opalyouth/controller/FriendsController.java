package com.lifeManager.opalyouth.controller;

import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.common.response.BaseResponse;
import com.lifeManager.opalyouth.dto.friends.BriefFriendsInfoResponse;
import com.lifeManager.opalyouth.dto.friends.DetailFriendsInfoResponse;
import com.lifeManager.opalyouth.dto.member.request.FriendsConditionRequest;
import com.lifeManager.opalyouth.service.FriendsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/friends")
public class FriendsController {

    private final FriendsService friendsService;

    @GetMapping("/refresh")
    public BaseResponse<String> refreshFriends(Principal principal) {
        try {
            friendsService.refreshFriends(principal);
            return new BaseResponse<>("새로고침에 성공하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping
    public BaseResponse<List<BriefFriendsInfoResponse>> getTodayFriends(Principal principal) {
        try {
            List<BriefFriendsInfoResponse> todayFriends = friendsService.getTodayFriends(principal);
            return new BaseResponse<>(todayFriends);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/details")
    public BaseResponse<DetailFriendsInfoResponse> getMemberDetails(Principal principal, @RequestParam String nickname) {
        try {
            DetailFriendsInfoResponse memberDetails = friendsService.getMemberDetails(principal, nickname);
            return new BaseResponse<>(memberDetails);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/relation-type")
    public BaseResponse<List<BriefFriendsInfoResponse>> recommendByRelationType(Principal principal) {
        try {
            List<BriefFriendsInfoResponse> recommendFriendsResponseList = friendsService.recommendByRelationType(principal);
            return new BaseResponse<>(recommendFriendsResponseList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/distance")
    public BaseResponse<List<BriefFriendsInfoResponse>> recommendByDistance(Principal principal, @RequestParam int distance) {
        try {
            List<BriefFriendsInfoResponse> recommendFriendsResponseList = friendsService.recommendByDistance(principal, distance);
            return new BaseResponse<>(recommendFriendsResponseList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/condition")
    public BaseResponse<List<BriefFriendsInfoResponse>> recommendByCondition(Principal principal, @RequestBody FriendsConditionRequest friendsConditionRequest) {
        try {
            List<BriefFriendsInfoResponse> briefFriendsInfoResponseList = friendsService.recommendByCondition(principal, friendsConditionRequest);
            return new BaseResponse<>(briefFriendsInfoResponseList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
