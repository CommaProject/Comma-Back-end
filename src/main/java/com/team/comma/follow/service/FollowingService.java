package com.team.comma.follow.service;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.follow.domain.Following;
import com.team.comma.follow.exception.FollowingException;
import com.team.comma.follow.repository.FollowingRepository;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

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

        saveFollowing(toUserEmail , fromUserEmail);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    public void saveFollowing(String toUserEmail , String fromUserEmail) throws AccountException {
        User userTo = userRepository.findByEmail(toUserEmail)
                .orElseThrow(() -> new AccountException("해당 사용자를 찾을 수 없습니다."));

        User userFrom = userRepository.findByEmail(fromUserEmail)
                .orElseThrow(() -> new AccountException("대상 사용자를 찾을 수 없습니다."));

        Following following = Following.createFollowing(userTo , userFrom);
        followingRepository.save(following);
    }

    public MessageResponse isFollowedUser(String token , String toUserEmail) {
        String fromUserEmail = jwtTokenProvider.getUserPk(token);
        if(isFollowed(toUserEmail , fromUserEmail)) {
            return MessageResponse.of(REQUEST_SUCCESS , true);
        }

        return MessageResponse.of(REQUEST_SUCCESS , false);
    }

    public boolean isFollowed(String toUserEmail , String fromUserEmail) {
        if(followingRepository.getFollowedMeUserByEmail(toUserEmail , fromUserEmail).isPresent()) {
            return true;
        }

        return false;
    }

}
