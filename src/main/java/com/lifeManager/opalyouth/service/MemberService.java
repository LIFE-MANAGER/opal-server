package com.lifeManager.opalyouth.service;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.dto.member.MemberSignupRequest;
import com.lifeManager.opalyouth.entity.*;
import com.lifeManager.opalyouth.repository.ImageRepository;
import com.lifeManager.opalyouth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.lifeManager.opalyouth.common.response.BaseResponseStatus.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 회원가입
     * @param memberSignupRequest
     * @throws BaseException - DATABASE_INSERT_ERROR
     */
    public void signup(MemberSignupRequest memberSignupRequest) throws BaseException {
        // 존재하는 계정인지 체크 -> 1. 활성화 된 계정이 존재 하면 EXIST_EMAIL, 2. 그 외는 계정이 존재하지만 활성화 되지 않은 상태(휴면) 이므로 INACTIVE_ACCOUNT
        if (memberRepository.existsByEmail(memberSignupRequest.getEmail())) {
            // 1번 경우
            if (memberRepository.existsByEmailAndState(memberSignupRequest.getEmail(), BaseEntity.State.ACTIVE)) {
                throw new BaseException(EXIST_EMAIL);
            }
            // 2번 경우
            throw new BaseException(INACTIVE_ACCOUNT);
        }
        // signUp을 기본 회원가입으로 할 시 LOCAL로 저장함
        String idWithProvider;
        if (memberSignupRequest.getIdWithProvider() == null) {
            idWithProvider = "LOCAL";
        } else idWithProvider = memberSignupRequest.getIdWithProvider(); // KAKAO, NAVER를 통하여 회원가입하는 경우 SignUpRequest에 담겨져 있음.

        // 회원 엔티티 생성
        Member memberEntity = Member.builder()
                .idWithProvider(idWithProvider)
                .nickname(memberSignupRequest.getNickname())
                .memberName(memberSignupRequest.getMemberName())
                .email(memberSignupRequest.getEmail())
                .password(bCryptPasswordEncoder.encode(memberSignupRequest.getPassword()))
                .phoneNum(memberSignupRequest.getPhoneNum())
                .job(memberSignupRequest.getJob())
                .introduction(memberSignupRequest.getIntroduction())
                .locationEnabled(memberSignupRequest.isLocationEnabled())
                .details(
                        Details.builder()
                                .relationType(memberSignupRequest.getDetails().getRelationType())
                                .isMarried(memberSignupRequest.getDetails().isMarried())
                                .hasChildren(memberSignupRequest.getDetails().isHasChildren())
                                .personality(memberSignupRequest.getDetails().getPersonality())
                                .hobby(memberSignupRequest.getDetails().getHobby())
                                .build()
                )
                .birth(
                        Birth.builder()
                                .birth(memberSignupRequest.getBirth())
                                .build()
                )
                .location(
                        Location.builder()
                                .latitude(memberSignupRequest.getLatitude())
                                .longitude(memberSignupRequest.getLongitude())
                                .build()
                )
                .subscriptionStatus(false)
                .build();

        // 회원 프로필 이미지 엔티티 생성
        Image imageEntity = Image.builder()
                .url(memberSignupRequest.getImgUrl())
                .member(memberEntity)
                .build();

        try {
            memberRepository.save(memberEntity);
            imageRepository.save(imageEntity);
        } catch (Exception e) {
            throw new BaseException(DATABASE_INSERT_ERROR);
        }
    }
}
