package com.lifeManager.opalyouth.nicknameTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeManager.opalyouth.common.response.BaseResponse;
import com.lifeManager.opalyouth.common.response.BaseResponseStatus;
import com.lifeManager.opalyouth.dto.member.request.MemberNicknameRequest;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    private NicknameTestHelper testHelper;

    @BeforeEach
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        testHelper = new NicknameTestHelper(mockMvc, objectMapper, memberRepository);

        // 새로운 사용자를 생성하고 로그인하여 토큰을 가져옴
        testHelper.createUser(SIGNUP_URL, TEST_NICKNAME, TEST_EMAIL, TEST_PASSWORD);
        token = testHelper.loginAndGetToken(LOGIN_URL, TEST_EMAIL, TEST_PASSWORD, TOKEN_HEADER, BEARER_PREFIX);
    }

    @Test
    @DisplayName("닉네임 수정 성공")
    public void TestUpdateNickname() throws Exception {
        // 닉네임 수정 날짜를 설정한 후 닉네임을 수정
        testHelper.setNicknameUpdateDate(TEST_NICKNAME, LocalDate.of(2024, 6, 1));

        MemberNicknameRequest nicknameRequest = new MemberNicknameRequest();
        NicknameTestHelper.setField(nicknameRequest, "nickname", "newNickname");

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
        String newNickname = "newNickname";
        MemberNicknameRequest nicknameRequest = new MemberNicknameRequest();
        NicknameTestHelper.setField(nicknameRequest, "nickname", newNickname);

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

        // 동일한 닉네임을 가진 사용자를 먼저 생성
        testHelper.createUser(SIGNUP_URL, newNickname, "new@naver.com", "1111111111");

        // 원래 사용자의 닉네임 수정 날짜를 설정
        testHelper.setNicknameUpdateDate(TEST_NICKNAME, LocalDate.of(2024, 6, 1));

        // 닉네임 수정 시도
        MemberNicknameRequest nicknameRequest = new MemberNicknameRequest();
        NicknameTestHelper.setField(nicknameRequest, "nickname", newNickname);

        mockMvc.perform(patch(NICKNAME_URL)
                        .header(TOKEN_HEADER, BEARER_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nicknameRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(new BaseResponse<>(BaseResponseStatus.EXIST_NICKNAME))));
    }
}