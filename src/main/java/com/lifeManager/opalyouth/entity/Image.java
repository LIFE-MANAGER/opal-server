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

    @ManyToOne
    @JoinColumn(name = "member_idx")
    private Member member;

    @Builder
    public Image(String url, Member member) {
        this.url = url;
        this.member = member;
    }

    public void setUrl(String changedUrl) {
        this.url = changedUrl;
        int id = member.getImageList().indexOf(this);
        member.getImageList().get(id).setUrl(changedUrl);
    }
}
