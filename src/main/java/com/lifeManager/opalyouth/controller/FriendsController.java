package com.lifeManager.opalyouth.controller;

import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.common.response.BaseResponse;
import com.lifeManager.opalyouth.dto.friends.BriefFriendsInfoResponse;
import com.lifeManager.opalyouth.dto.friends.DetailFriendsInfoResponse;
import com.lifeManager.opalyouth.service.FriendsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            DetailFriendsInfoResponse memberDetails = friendsService.getMemberDetails(nickname);
            return new BaseResponse<>(memberDetails);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

}
