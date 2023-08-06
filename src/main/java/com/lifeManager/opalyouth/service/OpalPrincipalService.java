package com.lifeManager.opalyouth.service;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.common.response.BaseResponseStatus;
import com.lifeManager.opalyouth.dto.security.OpalPrincipal;
import com.lifeManager.opalyouth.entity.Member;
import com.lifeManager.opalyouth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.lifeManager.opalyouth.common.response.BaseResponseStatus.NON_EXIST_USER;

@Slf4j
@RequiredArgsConstructor
@Service
public class OpalPrincipalService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("[OpalPrincipalService]>loadUserByUsername start");
        Member memberEntity = memberRepository.findByEmailAndState(username, BaseEntity.State.ACTIVE)
                .orElseThrow(() -> new BaseException(NON_EXIST_USER));
        log.info("[OpalPrincipal Service] FIND MEMBER SUCCESS");
        OpalPrincipal opalPrincipalByMemberEntity = OpalPrincipal.createOpalPrincipalByMemberEntity(memberEntity);
        log.info("[OpalPrincipal Service] : opalPrincipalEntity username = {}", opalPrincipalByMemberEntity.getUsername());
        log.info("[OpalPrincipal Service] : opalPrincipalEntity password = {}", opalPrincipalByMemberEntity.getPassword());
        log.info("[OpalPrincipal Service] : opalPrincipalEntity authorities = {}", opalPrincipalByMemberEntity.getAuthorities());
        return OpalPrincipal.createOpalPrincipalByMemberEntity(memberEntity);
    }
}
