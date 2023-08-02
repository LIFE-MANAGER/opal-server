package com.lifeManager.opalyouth.entity;


import com.lifeManager.opalyouth.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chatroom")
public class Chatroom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_name", nullable = false, length = 25)
    private String roomName;

    @Column(name = "type", nullable = false, length = 25)
    private String type;

    @Builder
    public Chatroom(String roomName, String type) {
        this.roomName = roomName;
        this.type = type;
    }
}
