package com.lifeManager.opalyouth.entity;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member_details")
public class Details extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "details")
    private Member member;

    @Column(name = "relation_type", length = 20)
    private String relationType;

    @Column(name = "marital_status")
    @Enumerated(value = EnumType.STRING)
    private MaritalStatus maritalStatus;

    @Column(name = "has_children")
    private boolean hasChildren;

    @Column(name = "personality", length = 20)
    private String personality;

    @Column(name = "hobby", length = 20)
    private String hobby;

    @Builder
    public Details(String relationType, MaritalStatus maritalStatus, boolean hasChildren, String personality, String hobby) {
        this.relationType = relationType;
        this.maritalStatus = maritalStatus;
        this.hasChildren = hasChildren;
        this.personality = personality;
        this.hobby = hobby;
    }

    public enum MaritalStatus {
        MARRIED,
        SINGLE,
        DIVORCED
    }

    public static MaritalStatus stringToMaritalStatus(String str) {
        System.out.println("str : " + str);
        return MaritalStatus.valueOf(str.toUpperCase());
    }
}
