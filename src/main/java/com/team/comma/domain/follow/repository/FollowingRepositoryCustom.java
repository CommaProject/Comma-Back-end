package com.team.comma.domain.follow.repository;

import com.team.comma.domain.user.domain.User;
import com.team.comma.domain.follow.dto.FollowingResponse;

import java.util.List;
import java.util.Optional;

public interface FollowingRepositoryCustom {

    Optional<User> getFollowedMeUserByEmail(String toUserEmail , String fromUserEmail);

    Optional<User> getBlockedUser(String toUserEmail , String fromUserEmail);

    void blockFollowedUser(String toUserEmail , String fromUserEmail);

    void unblockFollowedUser(String toUserEmail , String fromUserEmail);

    List<FollowingResponse> getFollowingUserListByUser(User user);

    List<FollowingResponse> getFollowedUserListByUser(User user);
}
