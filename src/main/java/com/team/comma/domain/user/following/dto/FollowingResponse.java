package com.team.comma.domain.user.following.dto;

import com.team.comma.domain.user.following.constant.FollowingType;
import com.team.comma.domain.user.following.domain.Following;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class FollowingResponse {

    private final long followingId;
    private final long userId;
    private final String userEmail;
    private final String userNickname;
    private final long followForFollow;

    public static FollowingResponse of(Following following, FollowingType type) {
        long userId;
        String userEmail, userNickname;
        if(type.equals(FollowingType.FOLLOWING)){
            userId = following.getUserTo().getId();
            userEmail = following.getUserTo().getEmail();
            userNickname = following.getUserTo().getUserDetail().getNickname();
        } else {
            userId = following.getUserFrom().getId();
            userEmail = following.getUserFrom().getEmail();
            userNickname = following.getUserFrom().getUserDetail().getNickname();
        }

        return FollowingResponse.builder()
                .followingId(following.getId())
                .userId(userId)
                .userEmail(userEmail)
                .userNickname(userNickname)
                .build();
    }
}
