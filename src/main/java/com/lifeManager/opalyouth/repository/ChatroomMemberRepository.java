package com.lifeManager.opalyouth.repository;

import com.lifeManager.opalyouth.entity.Chatroom;
import com.lifeManager.opalyouth.entity.ChatroomMember;
import com.lifeManager.opalyouth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatroomMemberRepository extends JpaRepository<ChatroomMember, Long> {
    List<ChatroomMember> findByMember(Member member);

}
