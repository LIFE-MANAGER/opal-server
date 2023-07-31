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

    @ManyToOne
    @JoinColumn(name = "blocked_member_Idx", nullable = false)
    private Member blockedMember; // 차단당한 회원

    @ManyToOne
    @JoinColumn(name = "member_idx", nullable = false)
    private Member member; // 차단한 회원

    @Builder
    public Block(Member blockedMember, Member member) {
        this.blockedMember = blockedMember;
        this.member = member;
        member.getBlockList().add(this); // 차단 친구 set 할 시 양방향 매핑의 차단한 사람의 BlockList 에 차단한 회원 추가.
    }

    /**
     * Setter of BlockedMember
     * 차단 친구 set 할 시 양방향 매핑의 차단한 사람의 BlockList 에 차단한 회원 추가.
     * @param blockedMember
     */
    public void setBlockedMember(Member blockedMember) {
        this.blockedMember = blockedMember;
        member.getBlockList().add(this);
    }

    /**
     * 차단 해제 시 회원의 BlockList 에서 blockedMember 삭제.
     * todo : Entity 자체 삭제도 구현.
     * @param blockedMember
     */
    public void unblockMember(Member blockedMember) {
        member.getBlockList().remove(this);
    }
}
