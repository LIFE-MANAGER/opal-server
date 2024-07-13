package com.lifeManager.opalyouth.signupTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeManager.opalyouth.common.response.BaseResponse;
import com.lifeManager.opalyouth.dto.member.request.MemberSignupRequest;
import com.lifeManager.opalyouth.entity.Member;
import com.lifeManager.opalyouth.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.lifeManager.opalyouth.common.response.BaseResponseStatus.*;


@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class CreateUserTest {

    @Autowired
    WebApplicationContext ctx;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberRepository memberRepository;

    final String SIGNUP_URI = "/signup";


    @BeforeEach
    public void setUp(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가 (한글 안깨지기 위함)
                .alwaysDo(print())
                .build();
    }

    private void createdUserSetDetails(MemberSignupRequest request) throws Exception{
        MemberSignupRequest.DetailsSignupRequest details = new MemberSignupRequest.DetailsSignupRequest();
        setField(details, "relationType", "FRIEND");
        setField(details, "maritalStatus", "MARRIED");
        setField(details, "hasChildren", false);
        setField(details, "personality", "ESTJ");
        setField(details, "hobby", "baseball");

        setField(request, "details", details);

    }

    private MemberSignupRequest createLocalUser(String nickname, String memberName, String email, String password, String phoneNumber, String introduction) throws Exception {
        MemberSignupRequest localSignupRequest = new MemberSignupRequest();
        setField(localSignupRequest, "nickname", nickname);
        setField(localSignupRequest, "memberName", memberName);
        setField(localSignupRequest, "email", email);
        setField(localSignupRequest, "password", password);
        setField(localSignupRequest, "phoneNum", phoneNumber);
        setField(localSignupRequest, "gender", "Male");
        setField(localSignupRequest, "job", "Engineer");
        setField(localSignupRequest, "introduction", introduction);
        setField(localSignupRequest, "locationEnabled", true);
        setField(localSignupRequest, "birth", LocalDate.of(1990, 1, 1));
        setField(localSignupRequest, "latitude", 37.5665);
        setField(localSignupRequest, "longitude", 126.9780);
        setField(localSignupRequest, "imgUrl", "http://example.com/johndoe.jpg");
        createdUserSetDetails(localSignupRequest);

        return localSignupRequest;
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
    private void postPerform(String url, Object dto) throws Exception {
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
    }

    private void postPerformAndResult(String url, Object dto, BaseResponse<?> baseResponse) throws Exception {
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(baseResponse)));
    }

    @Test
    @DisplayName("로컬 회원가입 성공 테스트")
    public void testCreateUserWithLocalSuccess() throws Exception {
        //로컬로 사용자 회원가입
        MemberSignupRequest request = createLocalUser(
                "lee",
                "asdasd" ,
                "123@naver.com",
                "1234",
                "010-1234-1234",
                "asd");
        //api test
        postPerformAndResult(SIGNUP_URI, request, new BaseResponse<>("회원가입에 성공하였습니다."));
    }

    @Test
    @DisplayName("이미 존재하는 계정 - 아이디 중복")
    void testCreateLocalUserAndIdDuplication() throws Exception {
        MemberSignupRequest member1 = createLocalUser(
                "lee",
                "asdasd123",
                "123@naver.com",
                "1234",
                "010-1234-1234",
                "");
        postPerform(SIGNUP_URI, member1);

        MemberSignupRequest member2 = createLocalUser(
                "asd",
                "asdasd",
                "123@naver.com",
                "1234",
                "010-1234-1234",
                "hello");

        postPerformAndResult(SIGNUP_URI, member2, new BaseResponse<>(EXIST_EMAIL));


    }

    @Test
    @DisplayName("이미 존재하는 계정 - 비활")
    void testCreateLocalUserAndUserInactive() throws Exception {
        MemberSignupRequest member = createLocalUser(
                "lee",
                "name",
                "123@naver.com",
                "1234",
                "010-1234-1234",
                "hello");
        postPerform(SIGNUP_URI, member);

        Member findMemberAndSetInactive = memberRepository.findByNickname("lee").get(0);
        findMemberAndSetInactive.setInActive();

        MemberSignupRequest existMember = createLocalUser(
                "asd",
                "name",
                "123@naver.com",
                "1234",
                "010-1234-1234",
                "hello");

        postPerformAndResult(SIGNUP_URI, existMember, new BaseResponse<>(INACTIVE_ACCOUNT));

    }

    @Test
    @DisplayName("닉네임 중복")
    public void testCreateLocalUserAndNicknameDuplication() throws Exception {
        String sameNickname = "lee";

        MemberSignupRequest member1 = createLocalUser(sameNickname,
                "name",
                "123@naver.com",
                "1234",
                "010-1234-1234",
                "hello");
        postPerform(SIGNUP_URI, member1);
        MemberSignupRequest member2WithSameNickname = createLocalUser(
                sameNickname,
                "name",
                "1234@naver.com",
                "1234",
                "010-1234-1234",
                "hello");

        postPerformAndResult(SIGNUP_URI, member2WithSameNickname, new BaseResponse<>(EXIST_NICKNAME));
    }

    @Test
    @DisplayName("핸드폰 형식 오류")
    public void testCreateLocalUserAndPhoneNumberFormatError() throws Exception {
        String wrongPhoneNumber = "010-32-3232";
        MemberSignupRequest memberWithWrongPhoneNumber = createLocalUser(
                "lee",
                "name",
                "123@naver.com",
                "1234",
                wrongPhoneNumber,
                "hello");
        postPerformAndResult(SIGNUP_URI,
                memberWithWrongPhoneNumber,
                new BaseResponse<>(false, 400, "must match \"^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$\""));
    }

    @Test
    @DisplayName("비밀번호 (@NotBlank)")
    public void testCreateLocalUserAndPasswordError() throws Exception {
        String wrongPassword = "";
        MemberSignupRequest memberWithPasswordBlank = createLocalUser(
                "lee",
                "name",
                "123@naver.com",
                wrongPassword,
                "010-1234-3234",
                "hello");
        postPerformAndResult(SIGNUP_URI,
                memberWithPasswordBlank,
                new BaseResponse<>(false, 400, "must not be blank"));

    }

    @Test
    @DisplayName("사용자 이름 (@NotBlank)")
    public void testCreateLocalUserAndUserNameError() throws Exception {
        String wrongUserName = "";
        MemberSignupRequest memberWithUserNameBlank = createLocalUser(
                "lee",
                wrongUserName,
                "123@naver.com",
                "1234",
                "010-1234-3234",
                "hello");
        postPerformAndResult(SIGNUP_URI,
                memberWithUserNameBlank,
                new BaseResponse<>(false, 400, "must not be blank"));

    }

    @Test
    @DisplayName("자기소개 형식 오류 (@NotBlank)")
    public void testCreateLocalUserAndIntroductionFormatError() throws Exception {
        String wrongIntroduction = "";
        MemberSignupRequest memberWithPasswordBlank = createLocalUser(
                "lee",
                "name",
                "123@naver.com",
                "1234",
                "010-1234-3234",
                wrongIntroduction);
        postPerformAndResult(SIGNUP_URI,
                memberWithPasswordBlank,
                new BaseResponse<>(false, 400, "must not be blank"));

    }

    @Test
    @DisplayName("이메일 형식 오류 (@Email)")
    public void testCreateLocalUserAndEmailFormatError() throws Exception {
        String wrongEmail = "leenaver.com";
        MemberSignupRequest memberWithPasswordBlank = createLocalUser(
                "lee",
                "name",
                wrongEmail,
                "1234",
                "010-1234-3234",
                "hello");
        postPerformAndResult(SIGNUP_URI,
                memberWithPasswordBlank,
                new BaseResponse<>(false, 400, "must be a well-formed email address"));

    }
}
