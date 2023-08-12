package com.lifeManager.opalyouth.repository;

import com.lifeManager.opalyouth.entity.Member;
import com.lifeManager.opalyouth.entity.TodaysFriends;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TodaysFriendsRepository extends JpaRepository<TodaysFriends, Long> {
    Optional<TodaysFriends> findByMember(Member member);
}
