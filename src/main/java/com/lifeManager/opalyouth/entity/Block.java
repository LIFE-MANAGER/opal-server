package com.lifeManager.opalyouth.entity;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "block_member")
public class Block extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "block_member_idx")
    private Long blockIdx;

    @Column(name = "blocked_member", nullable = false)
    private Long blockedMember;

    @ManyToOne
    @JoinColumn(name = "member_idx")
    private Member member;

    @Builder
    public Block(Long blockedMember, Member member) {
        this.blockedMember = blockedMember;
        this.member = member;
    }
}
