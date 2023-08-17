package com.team.comma.domain.follow.service;

import com.team.comma.domain.follow.constant.FollowingType;
import com.team.comma.domain.follow.domain.Following;
import com.team.comma.domain.follow.dto.FollowingResponse;
import com.team.comma.domain.follow.exception.FollowingException;
import com.team.comma.domain.follow.repository.FollowingRepository;
import com.team.comma.domain.user.domain.User;
import com.team.comma.domain.user.repository.UserRepository;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;

import java.util.ArrayList;
import java.util.List;

import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;


@Service
@RequiredArgsConstructor
public class FollowingService {

    private final FollowingRepository followingRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public MessageResponse blockFollowedUser(String token , String toUserEmail) {
        String fromUserEmail = jwtTokenProvider.getUserPk(token);
        followingRepository.blockFollowedUser(toUserEmail , fromUserEmail);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    public MessageResponse unblockFollowedUser(String token , String toUserEmail) {
        String fromUserEmail = jwtTokenProvider.getUserPk(token);
        followingRepository.unblockFollowedUser(toUserEmail , fromUserEmail);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    @Transactional
    public MessageResponse addFollow(String token , String toUserEmail) throws AccountException {
        String fromUserEmail = jwtTokenProvider.getUserPk(token);
        if(isFollowed(toUserEmail , fromUserEmail)) {
            throw new FollowingException("이미 팔로우중인 사용자입니다.");
        }
        if(isBlockedUser(toUserEmail , fromUserEmail)) {
            throw new FollowingException("차단된 사용자입니다.");
        }

        saveFollowing(toUserEmail , fromUserEmail);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    public void saveFollowing(String toUserEmail , String fromUserEmail) throws AccountException {
        User userTo = userRepository.findByEmail(toUserEmail)
                .orElseThrow(() -> new AccountException("해당 사용자를 찾을 수 없습니다."));

        User userFrom = userRepository.findByEmail(fromUserEmail)
                .orElseThrow(() -> new AccountException("대상 사용자를 찾을 수 없습니다."));

        Following following = Following.createFollowingToFrom(userTo , userFrom);
        followingRepository.save(following);
    }

    public MessageResponse isFollowedUser(String token , String toUserEmail) {
        String fromUserEmail = jwtTokenProvider.getUserPk(token);
        if(isFollowed(toUserEmail , fromUserEmail)) {
            return MessageResponse.of(REQUEST_SUCCESS , true);
        }

        return MessageResponse.of(REQUEST_SUCCESS , false);
    }

    public boolean isBlockedUser(String toUserEmail , String fromUserEmail) {
        if(followingRepository.getBlockedUser(toUserEmail , fromUserEmail).isPresent()) {
            return true;
        }

        return false;
    }

    public boolean isFollowed(String toUserEmail , String fromUserEmail) {
        if(followingRepository.getFollowedMeUserByEmail(toUserEmail , fromUserEmail).isPresent()) {
            return true;
        }

        return false;
    }

    public MessageResponse getFollowingUserList(String token, FollowingType followingType) throws AccountException {
        String userEmail = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AccountException("사용자 정보를 찾을 수 없습니다."));

        List<FollowingResponse> returnResponses;
        if (followingType.equals(FollowingType.FOLLOWING)) {
            returnResponses = getFollowingToUserListByFromUser(user);
        } else if (followingType.equals(FollowingType.FOLLOWED)) {
            returnResponses = getFollowingFromUserListByToUser(user);
        } else {
            List<FollowingResponse> followingResponses = getFollowingToUserListByFromUser(user);
            List<FollowingResponse> followedResponses = getFollowingFromUserListByToUser(user);

            returnResponses = getFollowingAndFollowedUserList(followingResponses, followedResponses);
        }

        return MessageResponse.of(REQUEST_SUCCESS, returnResponses);
    }

    public List<FollowingResponse> getFollowingToUserListByFromUser(User user) {
        return followingRepository.getFollowingToUserListByFromUser(user);
    }

    public List<FollowingResponse> getFollowingFromUserListByToUser(User user) {
        return followingRepository.getFollowingFromUserListByToUser(user);
    }

    public List<FollowingResponse> getFollowingAndFollowedUserList(
            List<FollowingResponse> followingResponses,
            List<FollowingResponse> followedResponses) {
        List<FollowingResponse> returnResponses = new ArrayList<>();
        for(FollowingResponse following : followingResponses){
            for(FollowingResponse followed : followedResponses){
                if(following.getUserId() == followed.getUserId()){
                    returnResponses.add(following);
                }
            }
        }
        return returnResponses;
    }

}
