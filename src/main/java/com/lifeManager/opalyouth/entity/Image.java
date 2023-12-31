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
@Table(name = "member_image")
public class Image extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", nullable = false)
    private String url;

    @OneToOne
    @JoinColumn(name = "member_idx")
    private Member member;

    @Builder
    public Image(String url, Member member) {
        this.url = url;
        this.member = member;
    }

    public void updateUrl(String changedUrl) {
        this.url = changedUrl;
        member.setImage(this);
    }
}
