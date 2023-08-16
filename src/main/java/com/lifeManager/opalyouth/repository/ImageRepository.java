package com.lifeManager.opalyouth.repository;

import com.lifeManager.opalyouth.entity.Image;
import com.lifeManager.opalyouth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByMember(Member member);
}
