package com.lifeManager.opalyouth.repository;

import com.lifeManager.opalyouth.common.entity.BaseEntity;
import com.lifeManager.opalyouth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmailAndState(String email, BaseEntity.State state);
    List<Member> findByNickname(String nickname);

    Optional<Member> findByNicknameAndState(String nickname, BaseEntity.State state);

    Boolean existsByEmail(String email);

    Boolean existsByEmailAndState(String email, BaseEntity.State state);

    Optional<Member> findByIdWithProviderAndState(String idWithProvider, BaseEntity.State state);

    List<Member> findAllByState(BaseEntity.State state);
}
