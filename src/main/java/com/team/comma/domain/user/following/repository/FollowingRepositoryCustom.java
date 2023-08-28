package com.team.comma.domain.user.following.repository;

import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.following.dto.FollowingResponse;

import java.util.List;
import java.util.Optional;

public interface FollowingRepositoryCustom {

    Optional<User> getFollowedMeUserByEmail(long toUserId , String fromUserEmail);

    Optional<User> getBlockedUser(long toUserId , String fromUserEmail);

    void blockFollowedUser(long followingId);

    void unblockFollowedUser(long followingId);

    List<FollowingResponse> getFollowingToUserListByFromUser(User user);

    List<FollowingResponse> getFollowingFromUserListByToUser(User user);
}
