package com.lifeManager.opalyouth.service;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.common.response.BaseResponseStatus;
import com.lifeManager.opalyouth.dto.member.*;
import com.lifeManager.opalyouth.entity.*;
import com.lifeManager.opalyouth.repository.BlockRepository;
import com.lifeManager.opalyouth.repository.FriendRequestRepository;
import com.lifeManager.opalyouth.repository.ImageRepository;
import com.lifeManager.opalyouth.entity.Block;
import com.lifeManager.opalyouth.entity.Details;
import com.lifeManager.opalyouth.entity.Member;
import com.lifeManager.opalyouth.repository.MemberRepository;
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
    private final FriendRequestRepository friendRequestRepository;

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
                        Location.builder()
                                .latitude(memberSignupRequest.getLatitude())
                                .longitude(memberSignupRequest.getLongitude())
                                .build()
                )
                .subscriptionStatus(false)
                .nicknameUpdateAt(LocalDate.now())
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
    // 회원 정보 (마이페이지)
    public MemberInfoResponse getMemberInfo(Principal principal) throws BaseException {
        Optional<Member> optional = memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE);
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NON_EXIST_USER);
        }

        Member member = optional.get();
        Details details = member.getDetails();

        Optional<Image> image = imageRepository.findById(member.getId());

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
    public void updateProfile(Principal principal, MemberProfileInfoRequest memberProfileInfoRequest) throws BaseException {
        Optional<Member> optional = memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE);
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NON_EXIST_USER);
        }

        Member member = optional.get();
        Details details = member.getDetails();

        log.info("memberId: {}", member.getId());

        try {
            member.setJob(memberProfileInfoRequest.getJob());
            member.setIntroduction(memberProfileInfoRequest.getIntroduction());
            details.setMarried(memberProfileInfoRequest.isMarried());
            details.setHasChildren(memberProfileInfoRequest.isHasChildren());
            details.setPersonality(memberProfileInfoRequest.getPersonality());
            details.setHobby(memberProfileInfoRequest.getHobby());

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
        Optional<Block> optionalBlock = blockRepository.findById(memberIdRequest.getId());
        if (optionalBlock.isEmpty()) {
            throw new BaseException(NON_EXIST_USER);
        }

        Member member = optional.get();
        List<Block> blockList = member.getBlockList();
        log.info("MEMBER : {}", member.getMemberName());

        log.info("Block List Before Remove : {}", blockList);
        blockList.removeIf(block -> block.getId().equals(memberIdRequest.getId()));
        log.info("Block List After Remove : {}", blockList);
        member.setBlockList(blockList);

        Block block = blockRepository.findById(memberIdRequest.getId())
                .orElseThrow(()-> new BaseException(NON_EXIST_USER));

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

    // 친구 요청하기 기능
    public void requestFriend(Principal principal, MemberIdRequest memberIdRequest) throws BaseException {
        Optional<Member> optional = memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE);
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NON_EXIST_USER);
        }

        // optionalMember: 요청 받은 사람
        Optional<Member> optionalMember = memberRepository.findById(memberIdRequest.getId());
        if (optionalMember.isEmpty()) {
            throw new BaseException(NON_EXIST_USER);
        }

        Member requestMember = optional.get();
        Member requestedMember = optionalMember.get();      // 요청 받은 사람

        log.info("REQUEST NAME: {}", requestMember.getMemberName());
        log.info("REQUESTED_MEMBER NAME: {}",requestedMember.getMemberName());

        List<FriendRequest> memberFriendRequestList = requestMember.getFriendRequestList();        // member가 요청받은 목록
        List<FriendRequest> friendRequestList = requestedMember.getFriendRequestList();     // requestedMember(상대)가 요청받은 목록


        // 같은 상대에게 이미 요청을 한 경우 (중복)
        boolean isAlreadyRequest = false;
        for (FriendRequest friendRequest : friendRequestList) {
            if (friendRequest.getRequestedMember().getId() == requestedMember.getId()) {
                log.info("[IS ALREADY REQUEST] MemberID: {}", requestMember.getId());
                log.info("[IS ALREADY REQUEST] RequestedMemberID: {}", requestedMember.getId());
                isAlreadyRequest = true;
                break;
            }
        }

        if (isAlreadyRequest) {
            throw new BaseException(ALREADY_REQUESTED);
        }


        // 내 친구요청 목록에 requestedMember의 id가 들어있을 때
        boolean bothFriendRequest = false;
        for(FriendRequest friendRequest : memberFriendRequestList) {
            if (friendRequest.getRequestMember().getId() == requestedMember.getId()) {
                bothFriendRequest = true;
                log.info("[BOTH FRIEND REQUEST] MemberID: {}", requestMember.getId());
                log.info("[BOTH FRIEND REQUEST] RequestedMemberID: {}", requestedMember.getId());
                break;
            }
        }

        FriendRequest friendRequest = FriendRequest.builder()
                .requestedMember(requestedMember)
                .requestMember(requestMember)
                .build();


        /**
         * 내 id가 requestedMember의 FriendRequest에 없는 경우
         * - FriendRequest에 나 추가
         */
        if (!bothFriendRequest) {
            try {
                requestMember.addFriendRequest(requestedMember);
                requestedMember.addFriendRequest(requestMember);
                friendRequestRepository.save(friendRequest);
                log.info("!bothFriendRequest");
            } catch (Exception e) {
                throw new BaseException(DATABASE_INSERT_ERROR);
            }
        }

        /**
         * 내 id가 requestedMember의 FriendRequest에 있는 경우
         * - 친구목록(Friends)에 추가
         * - FriendRequest에서 나 삭제
         */
        else {
            requestMember.addFriends(requestedMember);
            requestedMember.addFriends(requestMember);

            try {
                memberFriendRequestList.removeIf(request -> request.getRequestMember().getId().equals(memberIdRequest.getId()));
                friendRequestRepository.delete(friendRequest);
                memberRepository.save(requestMember);
                memberRepository.save(requestedMember);
            } catch (Exception e) {
                throw new BaseException(DATABASE_INSERT_ERROR);
            }

            throw new BaseException(MATCH);
        }
    }

    // 친구목록
    public List<FriendInfoResponse> getFriendsInfo(Principal principal) throws BaseException {
        Optional<Member> optional = memberRepository.findByEmailAndState(principal.getName(), BaseEntity.State.ACTIVE);
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.NON_EXIST_USER);
        }

        Member member = optional.get();

        List<Friends> friendsEntityList = member.getFriendsList();
        List<FriendInfoResponse> friendInfoResponseList = friendsEntityList.stream()
                .map(FriendInfoResponse::FriendEntityToFriendRes).collect(Collectors.toList());

        if (friendsEntityList.isEmpty()) {
            return Collections.emptyList();
        }
        return friendInfoResponseList;
    }
}
