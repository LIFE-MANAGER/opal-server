package com.lifeManager.opalyouth.entity;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.awt.*;
import java.awt.geom.Point2D;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member_location")
public class Location extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "location")
    private Point2D.Double location;

    @Builder
    public Location(@NotNull Double latitude, @NotNull Double longitude) {
        try {
            this.location = new Point2D.Double(latitude, longitude);
        } catch (NullPointerException e) {
            this.location = null;
        }
    }
}
