package com.lifeManager.opalyouth.entity;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.awt.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member_location")
public class Location extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_idx")
    private Long locationIdx;

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
