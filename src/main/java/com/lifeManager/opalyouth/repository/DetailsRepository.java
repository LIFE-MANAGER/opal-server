package com.lifeManager.opalyouth.repository;

import com.lifeManager.opalyouth.entity.Details;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetailsRepository extends JpaRepository<Details, Long> {
    List<Details> findByPersonality(String personality);
    List<Details> findByRelationType(String relationType);
    List<Details> findByHobby(String hobby);
}
