package com.lifeManager.opalyouth.service;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.common.response.BaseResponseStatus;
import com.lifeManager.opalyouth.dto.member.BlockedMemberResponse;
import com.lifeManager.opalyouth.dto.member.MemberIdRequest;
import com.lifeManager.opalyouth.dto.member.MemberInfoResponse;
import com.lifeManager.opalyouth.dto.member.MemberSignupRequest;
import com.lifeManager.opalyouth.entity.*;
import com.lifeManager.opalyouth.repository.BlockRepository;
import com.lifeManager.opalyouth.repository.ImageRepository;
import com.lifeManager.opalyouth.entity.Block;
import com.lifeManager.opalyouth.entity.Details;
import com.lifeManager.opalyouth.entity.Member;
import com.lifeManager.opalyouth.repository.MemberRepository;
import com.lifeManager.opalyouth.repository.TodaysFriendsRepository;
import com.lifeManager.opalyouth.utils.RefreshRecommendUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.lifeManager.opalyouth.common.response.BaseResponseStatus.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final BlockRepository blockRepository;
    private final RefreshRecommendUtils refreshRecommendUtils;
    private final TodaysFriendsRepository todaysFriendsRepository;

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
        // 닉네임 중복 확인
        List<Member> findMembers = this.memberRepository.findByNickname(memberSignupRequest.getNickname());
        if (!findMembers.isEmpty()) {
            throw new BaseException(BaseResponseStatus.EXIST_NICKNAME);
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
                        new Location(memberSignupRequest.getLatitude(), memberSignupRequest.getLongitude())
                )
                .subscriptionStatus(false)
                .nicknameUpdateAt(LocalDate.now())
                .build();

        // 회원 프로필 이미지 엔티티 생성
        Image imageEntity = Image.builder()
                .url(memberSignupRequest.getImgUrl())
                .member(memberEntity)
                .build();

        // 회원가입 시 오늘의 친구 생성 부분.
        Member recommendByPersonality = refreshRecommendUtils.createRecommendByPersonality(memberEntity.getDetails().getPersonality());
        log.info("RECOMMENDED BY PERSONAL : {}", recommendByPersonality.getMemberName());

        Member recommendByRelationType = refreshRecommendUtils.createRecommendByRelationType(memberEntity.getDetails().getRelationType());
        log.info("RECOMMENDED BY Rel : {}", recommendByRelationType.getMemberName());
        while (
                Objects.equals(recommendByRelationType.getId(), recommendByPersonality.getId())
        ) {
            recommendByRelationType = refreshRecommendUtils.createRecommendByRelationType(memberEntity.getDetails().getRelationType());
        }


        Member recommendByHobby = refreshRecommendUtils.createRecommendByHobby(memberEntity.getDetails().getHobby());
        log.info("RECOMMENDED BY Ho : {}", recommendByHobby.getMemberName());
        while (
                Objects.equals(recommendByHobby.getId(), recommendByRelationType.getId())
                        || Objects.equals(recommendByHobby.getId(), recommendByPersonality.getId())
        ) {
            recommendByHobby = refreshRecommendUtils.createRecommendByHobby(memberEntity.getDetails().getHobby());
        }

        TodaysFriends todaysFriends = new TodaysFriends(memberEntity, recommendByPersonality, recommendByRelationType, recommendByHobby);

        try {
            memberRepository.save(memberEntity);
            imageRepository.save(imageEntity);
            todaysFriendsRepository.save(todaysFriends);
        } catch (Exception e) {
            throw new BaseException(DATABASE_INSERT_ERROR);
        }
    }
    // 회원 정보 (마이페이지)
    public MemberInfoResponse getMemberInfo(Principal principal) throws BaseException {
        Optional<Member> optional = memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE);
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NON_EXIST_USER);
        }

        Member member = optional.get();
        Details details = member.getDetails();


        MemberInfoResponse memberInfoResponse = MemberInfoResponse.builder()
                .imageUrl(member.getImage().getUrl())
                .birth(member.getBirth().getBirth())
                .nickname(member.getNickname())
                .job(member.getJob())
                .introduction(member.getIntroduction())
                .isMarried(details.isMarried())
                .hasChildren(details.isHasChildren())
                .personality(details.getPersonality())
                .hobby(details.getHobby())
                .latitude(member.getLocation().getLatitude())
                .longitude(member.getLocation().getLongitude())
                .blockMemberNumber(member.getBlockList().size())
                .build();

        return memberInfoResponse;
    }



    // 프로필 사진 수정
    // todo: s3버킷 연동
    public void updateProfileImage(Principal principal, String imageUrl) throws BaseException {
        Optional<Member> optional = memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE);
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NON_EXIST_USER);
        }

        Member member = optional.get();
        Image memberImage = member.getImage();

        memberImage.updateUrl(imageUrl);
    }


    // 닉네임 수정
    public void updateNickname(Principal principal, String nickname) throws BaseException {
        Optional<Member> optional = this.memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE);
        if (optional.isEmpty()){
            throw new BaseException(BaseResponseStatus.NON_EXIST_USER);
        }

        Member member = optional.get();

        // 닉네임 수정 기간 확인
        LocalDate lastUpdate = member.getNicknameUpdateAt();
        log.info("[MEMBERSERVICE] : lastUpdate = {}", lastUpdate);
        Period between = Period.between(LocalDate.now(), lastUpdate);
        int amount = between.getDays();
        log.info("[MEMBERSERVICE] : amout = {}", amount);


        if (amount < 7) {
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

        log.info("memberId: {}", member.getId());

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
    public List<BlockedMemberResponse> getBlockedInfo(Principal principal) throws BaseException {
        Optional<Member> optional = memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE);
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NON_EXIST_USER);
        }

        Member member = optional.get();
        log.info("[MemberService] member = {}", member.getMemberName());
        List<Block> blockEntityList = member.getBlockList();
        List<BlockedMemberResponse> blockResponseList = blockEntityList.stream()
                .map(BlockedMemberResponse::BlockEntityToBlockRes)
                .collect(Collectors.toList());

        if (blockEntityList.isEmpty()) {
            return Collections.emptyList();
        }
        return blockResponseList;
    }

    // 차단 해제
    public void unblockMember(Principal principal, MemberIdRequest memberIdRequest) throws BaseException {
        Optional<Member> optional = memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE);
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NON_EXIST_USER);
        }
        Optional<Member> optionalBlock = memberRepository.findById(memberIdRequest.getId());
        if (optionalBlock.isEmpty()) {
            throw new BaseException(NON_EXIST_USER);
        }

        Member member = optional.get();
        List<Block> blockList = member.getBlockList();

        log.info("Block List Before Remove : {}", blockList);
        blockList.removeIf(block -> block.getBlockedMember().getId().equals(memberIdRequest.getId()));
        log.info("Block List After Remove : {}", blockList);
        member.setBlockList(blockList);

        Block block = blockRepository.findByMemberAndBlockedMember(member, optionalBlock.get())
                .orElseThrow(()-> new BaseException(NON_EXIST_USER));       // todo : Exception 이름 바꾸기

        try {
            blockRepository.delete(block);
            memberRepository.save(member);
        } catch (Exception e) {
            throw new BaseException(DATABASE_INSERT_ERROR);
        }
    }

    // 차단
    public void setBlockMember(Principal principal, MemberIdRequest memberIdRequest) throws BaseException {
        Optional<Member> optional = memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE);
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NON_EXIST_USER);
        }

        Optional<Member> blockMemberOptional = memberRepository.findById(memberIdRequest.getId());
        if (blockMemberOptional.isEmpty()) {
            throw new BaseException(NON_EXIST_USER);
        }

        Member member = optional.get();
        Member wantToBlock = blockMemberOptional.get();

        List<Block> blockList = member.getBlockList();

        boolean isAlreadyBlocked = blockList.stream()
                .anyMatch(block -> block.getBlockedMember().equals(wantToBlock));

        if (isAlreadyBlocked) {
            throw new BaseException(BaseResponseStatus.ALREADY_BLOCKED);
        }

        Block block = Block.builder()
                .blockedMember(wantToBlock)
                .member(member)
                .build();

        blockList.add(block);
        log.info("Member Block List Setter Before : {}", member.getBlockList());
        member.setBlockList(blockList);

        log.info("Member Block List Setter After : {}", member.getBlockList());

        try {
            blockRepository.save(block);
            memberRepository.save(member);
        } catch (Exception e) {
            throw new BaseException(DATABASE_INSERT_ERROR);
        }
    }
}
