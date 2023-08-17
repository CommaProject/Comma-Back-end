package com.team.comma.domain.follow.dto;

import com.team.comma.domain.follow.constant.FollowingType;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class FollowingRequest {
    private String toUserEmail;

    private FollowingType followingType;
}
