package com.lifeManager.opalyouth.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member_item")
public class item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "diamonds")
    private int diamonds;

    @Column(name = "megaphone")
    private int megaphone;

    @Builder
    public item(int diamonds, int megaphone) {
        this.diamonds = diamonds;
        this.megaphone = megaphone;
    }
}
