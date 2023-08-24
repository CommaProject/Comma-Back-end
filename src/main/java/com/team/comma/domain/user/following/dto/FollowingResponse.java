package com.team.comma.domain.user.following.dto;

import com.team.comma.domain.user.following.constant.FollowingType;
import com.team.comma.domain.user.following.domain.Following;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FollowingResponse {

    private final long followingId;
    private final long userId;
    private final String userNickname;

    private FollowingResponse(Following following, FollowingType type) {
        this.followingId = following.getId();
        if(type.equals(FollowingType.FOLLOWING)){
            this.userId = following.getUserTo().getId();
            this.userNickname = following.getUserTo().getUserDetail().getNickname();
        } else {
            this.userId = following.getUserFrom().getId();
            this.userNickname = following.getUserFrom().getUserDetail().getNickname();
        }
    }

    public static FollowingResponse of(Following following, FollowingType type){
        return new FollowingResponse(following, type);
    }
}
