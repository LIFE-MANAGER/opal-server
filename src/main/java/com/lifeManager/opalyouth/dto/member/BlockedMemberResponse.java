package com.lifeManager.opalyouth.dto.member;

import com.lifeManager.opalyouth.entity.Block;
import lombok.Getter;

@Getter
public class BlockedMemberResponse {
    private Long blockedMemberId;
    private String blockedMemberNickname;
    private String blockedMemberEmail;


    public BlockedMemberResponse() {
    }

    public BlockedMemberResponse(Long blockedMemberId, String blockedMemberNickname, String blockedMemberEmail) {
        this.blockedMemberId = blockedMemberId;
        this.blockedMemberNickname = blockedMemberNickname;
        this.blockedMemberEmail = blockedMemberEmail;
    }

    public static BlockedMemberResponse BlockEntityToBlockRes(Block block) {
        return new BlockedMemberResponse(
                block.getBlockedMember().getId(),
                block.getBlockedMember().getNickname(),
                block.getBlockedMember().getEmail()
        );
    }

}
