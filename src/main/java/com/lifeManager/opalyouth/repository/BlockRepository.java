package com.lifeManager.opalyouth.repository;

import com.lifeManager.opalyouth.entity.Block;
import com.lifeManager.opalyouth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    Optional<Block> findByMemberAndBlockedMember(Member member, Member blockedMember);
}
