package com.lifeManager.opalyouth.entity;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Member")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(name = "nickname", nullable = false, length = 25)
    private String nickname;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phoneNum", nullable = false)
    private String phoneNum;

    @Column(name = "details", nullable = false)
    private String details;

    @Column(name = "location_YN", nullable = false)
    private boolean locationEnabled;

    @Column(name = "subscribe", nullable = false)
    private boolean subscriptionStatus;
}
