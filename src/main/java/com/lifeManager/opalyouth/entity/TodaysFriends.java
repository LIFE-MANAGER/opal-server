package com.lifeManager.opalyouth.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "TODAYS_FRIENDS")
public class TodaysFriends {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_idx", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "member_by_personality")
    private Member memberByPersonality;

    @ManyToOne
    @JoinColumn(name = "member_by_relation_type")
    private Member memberByRelationType;

    @ManyToOne
    @JoinColumn(name = "member_by_hobby")
    private Member memberByHobby;


    public TodaysFriends() {
    }

    public TodaysFriends(Member member, Member memberByPersonality, Member memberByRelationType, Member memberByHobby) {
        this.member = member;
        this.memberByPersonality = memberByPersonality;
        this.memberByRelationType = memberByRelationType;
        this.memberByHobby = memberByHobby;
    }

    public void updateRecommends(Member memberByPersonality, Member memberByRelationType, Member memberByHobby) {
        this.memberByPersonality = memberByPersonality;
        this.memberByRelationType = memberByRelationType;
        this.memberByHobby = memberByHobby;
    }
}
