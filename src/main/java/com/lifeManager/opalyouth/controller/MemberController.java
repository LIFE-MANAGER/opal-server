package com.lifeManager.opalyouth.controller;

import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.common.response.BaseResponse;
import com.lifeManager.opalyouth.dto.member.MemberSignupRequest;
import com.lifeManager.opalyouth.repository.MemberRepository;
import com.lifeManager.opalyouth.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
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
}
