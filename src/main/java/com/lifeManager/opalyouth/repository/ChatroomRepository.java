package com.lifeManager.opalyouth.repository;

import com.lifeManager.opalyouth.entity.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {
    Optional<Chatroom> findByRoomId(String roomId);
}
