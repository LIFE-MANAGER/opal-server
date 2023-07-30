package com.lifeManager.opalyouth.entity;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_birth")
public class Birth extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "birth_idx")
    private Long birthIdx;

    @Column(name = "birth", nullable = false)
    private LocalDate birth;

    @OneToOne
    @JoinColumn(name = "member_idx")
    private Member member;

    @Builder
    public Birth(LocalDate birth, Member member) {
        this.birth = birth;
        this.member = member;
    }
}
