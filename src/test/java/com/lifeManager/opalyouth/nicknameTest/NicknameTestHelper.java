package com.lifeManager.opalyouth.nicknameTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeManager.opalyouth.dto.member.request.MemberNicknameRequest;
import com.lifeManager.opalyouth.dto.member.request.MemberSignupRequest;
import com.lifeManager.opalyouth.dto.security.LoginRequest;
import com.lifeManager.opalyouth.entity.Member;
import com.lifeManager.opalyouth.repository.MemberRepository;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NicknameTestHelper {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;

    public NicknameTestHelper(MockMvc mockMvc, ObjectMapper objectMapper, MemberRepository memberRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.memberRepository = memberRepository;
    }

    public void createUser(String signupUrl, String nickname, String email, String password) throws Exception {
        MemberSignupRequest signupRequest = createLocalUser(nickname, "user", email, password, "010-1234-1234", "intro");
        mockMvc.perform(post(signupUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());
    }

    public String loginAndGetToken(String loginUrl, String email, String password, String tokenHeader, String bearerPrefix) throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        setField(loginRequest, "email", email);
        setField(loginRequest, "password", password);

        MvcResult result = mockMvc.perform(post(loginUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        return result.getResponse().getHeader(tokenHeader).replace(bearerPrefix, "");
    }

    public void setNicknameUpdateDate(String nickname, LocalDate date) {
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

    public static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}