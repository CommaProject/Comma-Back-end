package com.team.comma.follow.dto;

import com.team.comma.follow.domain.Following;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FollowingResponse {

    private final long followingId;
    private final String fromUserNickname;
    private final String toUserNickname;

    private FollowingResponse(Following following) {
        this.followingId = following.getId();
        this.fromUserNickname = following.getUserFrom().getUserDetail().getNickname();
        this.toUserNickname = following.getUserTo().getUserDetail().getNickname();
    }

    public static FollowingResponse of(Following following){
        return new FollowingResponse(following);
    }
}
