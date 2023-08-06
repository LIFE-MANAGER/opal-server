package com.lifeManager.opalyouth.filter;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.common.response.BaseResponseStatus;
import com.lifeManager.opalyouth.dto.security.OpalPrincipal;
import com.lifeManager.opalyouth.entity.Member;
import com.lifeManager.opalyouth.repository.MemberRepository;
import com.lifeManager.opalyouth.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 인가 관련 구현
 * - Authorization 헤더의 bearer 토큰 확인
 * - token 으로부터 User의 email 추출
 * - email로 Database검색해 유저에 관한 정보를 SpringSecurityContextHolder에 넣음
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final MemberRepository memberRepository;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        log.info("[JwtAuthorizationFilter] Authorization start");
        String jwtHeader = request.getHeader("Authorization");

        // header 유무 확인
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")) {
            chain.doFilter(request, response);
            return;
        }

        String jwtToken = jwtHeader.replace("Bearer ", "");

        String userEmail = jwtUtils.getUserEmail(jwtToken);
        log.info("[JwtAuthorizationFilter] Valid Token");
        log.info("[JwtAuthorizationFilter] Access User Email = {}", userEmail);

        if (userEmail != null) {
            Member member = memberRepository
                    .findByEmailAndState(userEmail, BaseEntity.State.ACTIVE)
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.NON_EXIST_USER));
            log.info("memberId ; {}", member.getId());
            OpalPrincipal opalPrincipal = OpalPrincipal.createOpalPrincipalByMemberEntity(member);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    opalPrincipal,
                    null,
                    opalPrincipal.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("[JwtAuthorizationFilter] Authentication Set Successful");
            chain.doFilter(request, response);
        }
    }
}
