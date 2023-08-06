package com.lifeManager.opalyouth.dto.security;

import com.lifeManager.opalyouth.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
public class OpalPrincipal implements UserDetails, OAuth2User { // 인증, 인가를 위한 Principal
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private String nickname;
    @Getter private String email;
    private String introduction;
    private Map<String, Object> oAuth2Attributes;

    public static OpalPrincipal buildOpalPrincipalEntity(String username, String password, String email, String nickname, String introduction) {
        return OpalPrincipal.buildOpalPrincipalEntity(username, password, email, nickname, introduction, Map.of());
    }

    public static OpalPrincipal buildOpalPrincipalEntity(String username, String password, String email, String nickname, String introduction, Map<String, Object> oAuth2Attributes) {
        Set<RoleType> roleTypes = Set.of(RoleType.USER);
        return new OpalPrincipal(
                username,
                password,
                roleTypes.stream()
                        .map(RoleType::getName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toUnmodifiableSet()),
                nickname,
                email,
                introduction,
                oAuth2Attributes
        );
    }

    public static OpalPrincipal createOpalPrincipalByMemberEntity(Member member) {
        return OpalPrincipal.buildOpalPrincipalEntity(
                member.getEmail(),
                member.getPassword(),
                member.getEmail(),
                member.getNickname(),
                member.getIntroduction()
        );
    }

    public static OpalPrincipal createOpalPrincipalByMemberEntity(Member member, Map<String, Object> oAuth2Attributes) {
        return OpalPrincipal.buildOpalPrincipalEntity(
                member.getEmail(),
                member.getPassword(),
                member.getEmail(),
                member.getNickname(),
                member.getIntroduction(),
                oAuth2Attributes
        );
    }

    public enum RoleType {
        USER("ROLE_USER"),
        ADMIN("ROLE_ADMIN");

        @Getter
        private final String name;

        RoleType(String name) {
            this.name = name;
        }
    }

    @Override
    public String getName() { return username; }

    @Override
    public String getUsername() { return username; }

    @Override
    public String getPassword() { return password; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }


    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    @Override
    public Map<String, Object> getAttributes() { return oAuth2Attributes; }
}
