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
    private Long idx;

    @Column(name = "sexual_taste", length = 20)
    private String sexualTaste;

    @Column(name = "relation_type", length = 20)
    private String relationType;

    @Column(name = "is_married")
    private boolean isMarried;

    @Column(name = "has_children")
    private boolean hasChildren;

    @OneToOne
    @JoinColumn(name = "member_idx")
    private Member member;

    @Builder
    public Details(String sexualTaste, String relationType, boolean isMarried, boolean hasChildren, Member member) {
        this.sexualTaste = sexualTaste;
        this.relationType = relationType;
        this.isMarried = isMarried;
        this.hasChildren = hasChildren;
        this.member = member;
    }
}
