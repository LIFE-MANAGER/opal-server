package com.lifeManager.opalyouth.repository;

import com.lifeManager.opalyouth.entity.Location;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query("SELECT l FROM Location l WHERE FUNCTION('ST_Distance_Sphere', l.point, ST_Transform(:point, 4326)) <= :distance * 1000")
    List<Location> findLocationsWithinDistance(@Param("point") Point point, @Param("distance") Integer distance);
}
