package com.lifeManager.opalyouth.repository;

import com.lifeManager.opalyouth.entity.Friends;
import com.lifeManager.opalyouth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friends, Long> {
    Optional<Friends> findByFriendAndMember(Member friend, Member member);
}
