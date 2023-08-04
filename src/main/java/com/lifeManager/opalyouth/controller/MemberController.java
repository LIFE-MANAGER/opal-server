package com.lifeManager.opalyouth.controller;

import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.common.response.BaseResponse;
import com.lifeManager.opalyouth.dto.MemberDto;
import com.lifeManager.opalyouth.dto.MemberInfoResponse;
import com.lifeManager.opalyouth.entity.Block;
import com.lifeManager.opalyouth.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

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



    // 닉네임 수정
    @PatchMapping("/nickname")
    public BaseResponse<String> updateNickname(Principal principal, @RequestBody MemberDto.ReqNickname nickname) {
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
    public BaseResponse<List<Block>> getBlockedMember(Principal principal) {
        try {
            List<Block> blockList = memberService.getBlockedInfo(principal);
            return new BaseResponse<>(blockList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    // 차단 해제

}
