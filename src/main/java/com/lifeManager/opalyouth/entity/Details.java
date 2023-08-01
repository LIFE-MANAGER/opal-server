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
@Table(name = "member_details")
public class Details extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "details_idx")
    private Long detailsIdx;

    @Column(name = "sexual_taste", length = 20)
    private String sexualTaste;

    @Column(name = "relation_type", length = 20)
    private String relationType;

    @Column(name = "is_married")
    private boolean isMarried;

    @Column(name = "has_children")
    private boolean hasChildren;

    @Column(name = "personality", length = 20)
    private String personality;

    @Column(name = "hobby", length = 20)
    private String hobby;

    @OneToOne
    @JoinColumn(name = "member_idx")
    private Member member;

    @Builder
    public Details(String sexualTaste, String relationType, boolean isMarried, boolean hasChildren, String personality, String hobby, Member member) {
        this.sexualTaste = sexualTaste;
        this.relationType = relationType;
        this.isMarried = isMarried;
        this.hasChildren = hasChildren;
        this.personality = personality;
        this.hobby = hobby;
        this.member = member;
    }
}
