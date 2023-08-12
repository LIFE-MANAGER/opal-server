package com.lifeManager.opalyouth.repository;

import com.lifeManager.opalyouth.entity.FriendRequest;
import com.lifeManager.opalyouth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    Optional<FriendRequest> findByRequestedMemberAndRequestMember(Member requestedMember, Member requestMEmber);
}
