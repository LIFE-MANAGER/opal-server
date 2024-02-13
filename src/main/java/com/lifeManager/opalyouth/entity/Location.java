package com.lifeManager.opalyouth.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lifeManager.opalyouth.common.entity.BaseEntity;
import lombok.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import javax.persistence.*;
import java.awt.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member_location")
public class Location extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @Column(name = "location", columnDefinition = "POINT SRID 4326")
    private Point point;

    @OneToOne(mappedBy = "location")
    private Member member;

    @Builder
    public Location(Double latitude, Double longitude) {
        this.point = createPoint(latitude, longitude);
    }

    public static Point createPoint(double latitude, double longitude) {
        GeometryFactory geometryFactory = new GeometryFactory(
                new PrecisionModel(), 4326
        );
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }

    public Double getLatitude() { return this.point.getY(); }

    public Double getLongitude() { return this.point.getX(); }


    public static Double getDistance(Point point1, Point point2) {
        double distance = point1.distance(point2);
        return distance / 1000;
    }
}
