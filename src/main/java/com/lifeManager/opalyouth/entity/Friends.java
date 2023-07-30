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
@Table(name = "friends")
public class Friends extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friends_idx")
    private Long friendsIdx;

    @Column(name = "friend", nullable = false)
    private Long friend;

    @ManyToOne
    @JoinColumn(name = "member_idx")
    private Member member;

    @Builder
    public Friends(Long friend, Member member) {
        this.friend = friend;
        this.member = member;
    }
}
