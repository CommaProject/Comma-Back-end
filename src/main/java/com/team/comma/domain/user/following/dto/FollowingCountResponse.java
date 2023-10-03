package com.team.comma.domain.user.following.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FollowingCountResponse {
    private int followings;
    private int followers;

    private FollowingCountResponse (int followings, int followers) {
        this.followings = followings;
        this.followers = followers;
    }

    public static FollowingCountResponse of(int followings, int followers) {
        return new FollowingCountResponse(followings, followers);
    }
}
