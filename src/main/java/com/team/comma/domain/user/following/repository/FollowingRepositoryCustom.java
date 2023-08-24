package com.team.comma.domain.user.following.repository;

import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.following.dto.FollowingResponse;

import java.util.List;
import java.util.Optional;

public interface FollowingRepositoryCustom {

    Optional<User> getFollowedMeUserByEmail(String toUserEmail , String fromUserEmail);

    Optional<User> getBlockedUser(String toUserEmail , String fromUserEmail);

    void blockFollowedUser(String toUserEmail , String fromUserEmail);

    void unblockFollowedUser(String toUserEmail , String fromUserEmail);

    List<FollowingResponse> getFollowingToUserListByFromUser(User user);

    List<FollowingResponse> getFollowingFromUserListByToUser(User user);
}
