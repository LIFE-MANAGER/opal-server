package com.lifeManager.opalyouth.repository;

import com.lifeManager.opalyouth.entity.Friends;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friends, Long> {
}
