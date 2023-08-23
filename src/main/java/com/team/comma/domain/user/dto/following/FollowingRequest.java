package com.team.comma.domain.user.dto.following;

import com.team.comma.domain.user.constant.FollowingType;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class FollowingRequest {
    private String toUserEmail;

    private FollowingType followingType;
}
