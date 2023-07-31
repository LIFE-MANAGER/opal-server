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
@Table(name = "report")
public class Report extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_idx")
    private Long reportIdx;

    @Column(name = "content", nullable = false)
    private String content; // 신고 내용

    @ManyToOne
    @JoinColumn(name = "member_idx")
    private Member member; //신고한 사용자

    @ManyToOne
    @JoinColumn(name = "reported_member_idx")
    private Member reportedMember; // 신고당한 사용자

    @Builder
    public Report(String content, Member member, Member reportedMember) {
        this.content = content;
        this.member = member;
        this.reportedMember = reportedMember;
    }
}
