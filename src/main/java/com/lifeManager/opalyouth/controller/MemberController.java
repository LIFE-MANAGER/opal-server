package com.lifeManager.opalyouth.controller;

import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.common.response.BaseResponse;
import com.lifeManager.opalyouth.dto.member.*;
import org.springframework.validation.BindingResult;

import com.lifeManager.opalyouth.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public BaseResponse<String> signup(@Valid @RequestBody MemberSignupRequest memberSignupRequest, BindingResult result) {
        if (result.hasErrors()) {
            String message = result.getFieldError().getDefaultMessage();
            return new BaseResponse<>(false, 400, message);
        }

        try {
            memberService.signup(memberSignupRequest);
            return new BaseResponse<>("회원가입에 성공하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 마이페이지
    @GetMapping("/mypage")
    public BaseResponse<MemberInfoResponse> getMyPageInfo(Principal principal) {
        try {
            MemberInfoResponse memberInfoResponse = memberService.getMemberInfo(principal);
            return new BaseResponse<>(memberInfoResponse);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 프로필 이미지 수정
    @PatchMapping("/profile-image")
    public BaseResponse<String> updateImage(Principal principal, @RequestBody MemberImageRequest memberImageRequest) {
        try {
            memberService.updateProfileImage(principal, memberImageRequest.getImageUrl());
            return new BaseResponse<>("이미지 수정에 성공하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    // 닉네임 수정
    @PatchMapping("/nickname")
    public BaseResponse<String> updateNickname(Principal principal, @RequestBody MemberNicknameRequest nickname) {
        try {
            memberService.updateNickname(principal, nickname.getNickname());
            return new BaseResponse<>("닉네임을 수정하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 프로필 수정
    @PatchMapping("/profile")
    public BaseResponse<String> updateProfile(Principal principal, @RequestBody MemberInfoResponse memberInfoResponse) {
        try {
            memberService.updateProfile(principal, memberInfoResponse);
            return new BaseResponse<>("프로필을 수정하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 차단목록 반환
    @GetMapping("/blocked-member")
    public BaseResponse<List<BlockedMemberResponse>> getBlockedMember(Principal principal) {
        try {
            List<BlockedMemberResponse> blockList = memberService.getBlockedInfo(principal);
            return new BaseResponse<>(blockList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 차단 해제
    @PatchMapping("/unblock-member")
    public BaseResponse<String> unblockMember(Principal principal, @RequestBody MemberIdRequest memberId) {
        try {
            memberService.unblockMember(principal, memberId);
            return new BaseResponse<>("차단을 해제하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 차단
    @PostMapping("/block")
    public BaseResponse<String> setBlockMember(Principal principal, @RequestBody MemberIdRequest memberId) {
        try {
            memberService.setBlockMember(principal, memberId);
            return new BaseResponse<>("차단에 성공하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
