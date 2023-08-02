package com.lifeManager.opalyouth.repository;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import com.lifeManager.opalyouth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmailAndState(String email, BaseEntity.State state);

}
