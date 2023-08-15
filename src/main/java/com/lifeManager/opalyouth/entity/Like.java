package com.lifeManager.opalyouth.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member_like")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_idx", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "liked_member", nullable = false)
    private Member likedMember;     // 내가 호감 표시한 회원

    @Builder
    public Like(Member member, Member likedMember) {
        this.member = member;
        this.likedMember = likedMember;
    }
}
