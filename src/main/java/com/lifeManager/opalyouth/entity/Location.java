package com.lifeManager.opalyouth.entity;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import lombok.Builder;

import javax.persistence.*;
import java.awt.*;

public class Location extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(name = "location", nullable = false)
    private Point location;

    @OneToOne
    @JoinColumn(name = "member_idx")
    private Member member;

    @Builder
    public Location(Point location, Member member) {
        this.location = location;
        this.member = member;
    }
}
