package com.lifeManager.opalyouth.nicknameTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeManager.opalyouth.common.response.BaseResponse;
import com.lifeManager.opalyouth.common.response.BaseResponseStatus;
import com.lifeManager.opalyouth.dto.member.request.MemberNicknameRequest;
import com.lifeManager.opalyouth.dto.member.request.MemberSignupRequest;
import com.lifeManager.opalyouth.dto.security.LoginRequest;
import com.lifeManager.opalyouth.entity.Member;
import com.lifeManager.opalyouth.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
public class NicknameUpdateTest {
    @Autowired
    private WebApplicationContext ctx;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;

    private static final String SIGNUP_URL = "/signup";
    private static final String LOGIN_URL = "/login";
    private static final String NICKNAME_URL = "/nickname";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TEST_EMAIL = "123@naver.com";
    private static final String TEST_PASSWORD = "1234";
    private static final String TOKEN_HEADER = "Authorization";
    private static final String TEST_NICKNAME = "park";

    private String token;

    @BeforeEach
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .alwaysDo(print())
                .build();

        //새로윤 유저를 회원가입하고
        createUser(TEST_NICKNAME, TEST_EMAIL, TEST_PASSWORD);

        //새로운 유저를 로그인한 후 토큰 값 추출
        loginAndGetToken(TEST_EMAIL, TEST_PASSWORD);
    }

    @Test
    @DisplayName("닉네임 수정 성공")
    public void TestUpdateNickname() throws Exception {

        //닉네임 수정 날짜 변경 후 닉네임 변경
        setNicknameUpdateDate(TEST_NICKNAME, LocalDate.of(2024, 6, 1));

        MemberNicknameRequest nicknameRequest = new MemberNicknameRequest();
        setField(nicknameRequest, "nickname", "newNickname");

        mockMvc.perform(patch(NICKNAME_URL)
                        .header(TOKEN_HEADER, BEARER_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nicknameRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(new BaseResponse<>("닉네임을 수정하였습니다."))));
    }

    @Test
    @DisplayName("닉네임 수정 기간 오류")
    public void TestUpdateNicknameLimitNicknameDateError() throws Exception {

        //닉네임 수정 날짜 변경하지 않음
        String newNickname = "newNickname";
        MemberNicknameRequest nicknameRequest = new MemberNicknameRequest();
        setField(nicknameRequest, "nickname", newNickname);

        mockMvc.perform(patch(NICKNAME_URL)
                        .header(TOKEN_HEADER, BEARER_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nicknameRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(new BaseResponse<>(BaseResponseStatus.LIMIT_NICKNAME_CHANGE))));
    }

    @Test
    @DisplayName("이미 존재하는 닉네임")
    public void TestUpdateNicknameAndExistNicknameError() throws Exception {
        String newNickname = "sameNickname";

        //먼저 같은 닉네임을 가니 유저를 하나 만들고
        createUser(newNickname, "new@naver.com", "1111111111");

        //위에서 만든 유저의 닉네임 수정 날짜를 변경한 후
        setNicknameUpdateDate(TEST_NICKNAME, LocalDate.of(2024, 6, 1));

        //닉네임 수정 요청
        MemberNicknameRequest nicknameRequest = new MemberNicknameRequest();
        setField(nicknameRequest, "nickname", newNickname);

        mockMvc.perform(patch(NICKNAME_URL)
                        .header(TOKEN_HEADER, BEARER_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nicknameRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(new BaseResponse<>(BaseResponseStatus.EXIST_NICKNAME))));
    }

    private void createUser(String nickname, String email, String password) throws Exception {
        MemberSignupRequest signupRequest = createLocalUser(nickname, "user", email, password, "010-1234-1234", "intro");
        mockMvc.perform(post(SIGNUP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)));
    }

    private void loginAndGetToken(String email, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        setField(loginRequest, "email", email);
        setField(loginRequest, "password", password);

        MvcResult result = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        token = result.getResponse().getHeader(TOKEN_HEADER).replace(BEARER_PREFIX, "");
        System.out.println("token : " + token);
    }

    private void setNicknameUpdateDate(String nickname, LocalDate date) {
        Member member = memberRepository.findByNickname(nickname).get(0);
        member.setNicknameUpdateAt(date);
        memberRepository.save(member);
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

    private void createdUserSetDetails(MemberSignupRequest request) throws Exception {
        MemberSignupRequest.DetailsSignupRequest details = new MemberSignupRequest.DetailsSignupRequest();
        setField(details, "relationType", "FRIEND");
        setField(details, "maritalStatus", "MARRIED");
        setField(details, "hasChildren", false);
        setField(details, "personality", "ESTJ");
        setField(details, "hobby", "baseball");

        setField(request, "details", details);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}