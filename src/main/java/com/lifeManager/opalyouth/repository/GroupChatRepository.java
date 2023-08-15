package com.lifeManager.opalyouth.repository;

import com.lifeManager.opalyouth.entity.GroupChat;
import com.lifeManager.opalyouth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupChatRepository extends JpaRepository<GroupChat, Long> {
    Optional<GroupChat> findByMember(Member member);
}
