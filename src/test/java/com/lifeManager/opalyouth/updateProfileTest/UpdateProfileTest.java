package com.lifeManager.opalyouth.updateProfileTest;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.common.response.BaseResponseStatus;
import com.lifeManager.opalyouth.dto.member.request.MemberProfileInfoRequest;
import com.lifeManager.opalyouth.entity.Details;
import com.lifeManager.opalyouth.entity.Member;
import com.lifeManager.opalyouth.repository.MemberRepository;
import com.lifeManager.opalyouth.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class UpdateProfileTest {
    @Mock private MemberRepository memberRepository;
    @InjectMocks private MemberService memberService;

    private final String TESTER_EMAIL = "test@test.com";
    private Member savedMember;
    private MemberProfileInfoRequest req;

    @BeforeEach
    void setUp() {
        savedMember = Member.builder().job("job").introduction("intro").build();
        Details details = Details.builder()
                .maritalStatus(Details.MaritalStatus.SINGLE)
                .hasChildren(false)
                .personality("personality")
                .hobby("hobby")
                .build();
        savedMember.setDetails(details);
        req = createMemberProfileInfoRequest(
                "changed job",
                "ichanged ntroduction",
                "MARRIED",
                true,
                "persionallity",
                "hobby");
    }

    @Test
    @DisplayName("정상적인 요청 시 업데이트 성공")
    public void givenValidRequest_whenUpdateProfile_thenUpdate() {
        // given
        Principal principal = () -> TESTER_EMAIL;
        Mockito.when(memberRepository.findByEmailAndState(TESTER_EMAIL, BaseEntity.State.ACTIVE))
                .thenReturn(Optional.of(savedMember));

        // when
        memberService.updateProfile(principal, req);

        // then
        Mockito.verify(memberRepository).save(savedMember);
        assertThat(savedMember.getJob()).isEqualTo(req.getJob());
        assertThat(savedMember.getIntroduction()).isEqualTo(req.getIntroduction());
        assertThat(savedMember.getDetails().getMaritalStatus().toString()).isEqualTo(req.getMaritalStatus());
        assertThat(savedMember.getDetails().isHasChildren()).isEqualTo(req.isHasChildren());
        assertThat(savedMember.getDetails().getPersonality()).isEqualTo(req.getPersonality());
        assertThat(savedMember.getDetails().getHobby()).isEqualTo(req.getHobby());
    }

    @Test
    @DisplayName("유효하지 않은 이메일로 요청 시 발생")
    public void givenInvalidEmail_whenUpdateProfile_thenThrowException() {
        // given
        Principal principal = () -> TESTER_EMAIL;
        Mockito.when(memberRepository.findByEmailAndState(TESTER_EMAIL, BaseEntity.State.ACTIVE))
                .thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> memberService.updateProfile(principal, req))
                .isInstanceOf(BaseException.class)
                .hasMessage(BaseResponseStatus.NON_EXIST_USER.getMessage())
                .hasFieldOrPropertyWithValue("status", BaseResponseStatus.NON_EXIST_USER);
    }

    @Test
    @DisplayName("데이터 베이스 삽입할 경우 에러 발생 시 예외 발생")
    public void givenValidRequest_whenOccurDBError_thenThrowException() {
        // given
        Principal principal = () -> TESTER_EMAIL;
        Mockito.when(memberRepository.findByEmailAndState(TESTER_EMAIL, BaseEntity.State.ACTIVE))
                .thenReturn(Optional.of(savedMember));

        // when
        Mockito.when(memberRepository.save(any(Member.class)))
                .thenThrow(new RuntimeException());

        // then
        assertThatThrownBy(() -> memberService.updateProfile(principal, req))
                .isInstanceOf(BaseException.class)
                .hasMessage(BaseResponseStatus.DATABASE_INSERT_ERROR.getMessage())
                .hasFieldOrPropertyWithValue("status", BaseResponseStatus.DATABASE_INSERT_ERROR);
    }

    MemberProfileInfoRequest createMemberProfileInfoRequest(String job,
                                                            String introduction,
                                                            String maritalStatus,
                                                            boolean hasChildren,
                                                            String personallity,
                                                            String hobby) {
        MemberProfileInfoRequest req = new MemberProfileInfoRequest();
        req.setJob(job);
        req.setIntroduction(introduction);
        req.setMaritalStatus(maritalStatus);
        req.setHasChildren(hasChildren);
        req.setPersonality(personallity);
        req.setHobby(hobby);
        return req;
    }
}
