package com.lifeManager.opalyouth.service;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.common.response.BaseResponseStatus;
import com.lifeManager.opalyouth.dto.MemberInfoResponse;
import com.lifeManager.opalyouth.entity.Block;
import com.lifeManager.opalyouth.entity.Details;
import com.lifeManager.opalyouth.entity.Member;
import com.lifeManager.opalyouth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    // 회원 정보 (마이페이지)
    public MemberInfoResponse getMemberInfo(Principal principal) throws BaseException {
        Optional<Member> optional = memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE);
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NON_EXIST_USER);
        }

        Member member = optional.get();
        Details details = member.getDetails();

        MemberInfoResponse memberInfoResponse = MemberInfoResponse.builder()
                .imageList(member.getImageList())
                .birth(member.getBirth().getBirth())
                .nickname(member.getNickname())
                .job(member.getJob())
                .introduction(member.getIntroduction())
                .isMarried(details.isMarried())
                .hasChildren(details.isHasChildren())
                .personality(details.getPersonality())
                .hobby(details.getHobby())
                .build();

        return memberInfoResponse;
    }


    /*
    // 프로필 사진 수정;;
    // todo: s3버킷 연동
    public void updateProfileImage(Principal principal, String imageUrl) throws BaseException {
        Optional<Member> optional = memberRepository.findByEmailAndState(principal.getName(), baseEntity.getState());
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NON_EXIST_USER);
        }

        Member member = optional.get();
    }
    */

    // 닉네임 수정
    public void updateNickname(Principal principal, String nickname) throws BaseException {
        Optional<Member> optional = this.memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE);
        if (optional.isEmpty()){
            throw new BaseException(BaseResponseStatus.NON_EXIST_USER);
        }

        Member member = optional.get();

        // 닉네임 수정 기간 확인
        LocalDate lastUpdate = member.getNicknameUpdateAt();
        long days = ChronoUnit.DAYS.between(lastUpdate, LocalDate.now());

        if (days < 7) {
            throw new BaseException(BaseResponseStatus.LIMIT_NICKNAME_CHANGE);
        }

        // 닉네임 중복 확인
        List<Member> findMembers = this.memberRepository.findByNickname(nickname);
        if (!findMembers.isEmpty()) {
            throw new BaseException(BaseResponseStatus.EXIST_NICKNAME);
        }

        try {
            member.setNickname(nickname);
            member.setNicknameUpdateAt(LocalDate.now());
            memberRepository.save(member);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_INSERT_ERROR);
        }
    }


    // 직업, 결혼/자녀 유무, 성격, 취미, 취향(?), 자기소개 수정
    public void updateProfile(Principal principal, MemberInfoResponse memberInfoResponse) throws BaseException {
        Optional<Member> optional = memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE);
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NON_EXIST_USER);
        }

        Member member = optional.get();
        Details details = member.getDetails();

        try {
            member.setJob(memberInfoResponse.getJob());
            member.setIntroduction(memberInfoResponse.getIntroduction());
            details.setMarried(memberInfoResponse.isMarried());
            details.setHasChildren(memberInfoResponse.isHasChildren());
            details.setPersonality(memberInfoResponse.getPersonality());
            details.setHobby(memberInfoResponse.getHobby());

            memberRepository.save(member);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_INSERT_ERROR);
        }
    }

    
    // 차단 친구 반환
    public List<Block> getBlockedInfo(Principal principal) throws BaseException {
        Optional<Member> optional = memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE);
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NON_EXIST_USER);
        }

        Member member = optional.get();
        List<Block> blockedMember = member.getBlockList();

        if (blockedMember.isEmpty()) {
            return Collections.emptyList();
        }
        return blockedMember;
    }

    // 차단 해제
    public void releaseBlockedMember(Principal principal, Member blockedMember) throws BaseException {
        Optional<Member> optional = memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE);
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NON_EXIST_USER);
        }

        Member member = optional.get();
        List<Block> blockList = member.getBlockList();

        Optional<Block> optionalBlock = blockList.stream()
                .filter(block -> block.getBlockedMember().equals(blockedMember))
                .findFirst();

        if (optionalBlock.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NON_EXIST_USER);
        }

        blockList.remove(optionalBlock.get());
        try {
            member.setBlockList(blockList);
            memberRepository.save(member);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_INSERT_ERROR);
        }
    }
}
