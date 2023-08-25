package com.team.comma.domain.user.following.dto;

import com.team.comma.domain.user.following.constant.FollowingType;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class FollowingRequest {
    private String toUserEmail;
}
