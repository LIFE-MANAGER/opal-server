package com.lifeManager.opalyouth.repository;

import com.lifeManager.opalyouth.entity.Chatroom;
import com.lifeManager.opalyouth.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatroomOrderByCreatedAt(Chatroom chatroom);
}
