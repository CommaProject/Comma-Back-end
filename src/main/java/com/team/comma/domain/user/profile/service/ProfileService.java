package com.team.comma.domain.user.profile.service;

import com.team.comma.domain.user.profile.domain.UserDetail;
import com.team.comma.domain.user.profile.dto.UserDetailRequest;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.exception.UserException;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;

import static com.team.comma.global.common.constant.ResponseCodeEnum.NOT_FOUNT_USER;
import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public MessageResponse createProfile(final UserDetailRequest userDetailRequest, final String token)
            throws AccountException {
        if (token == null) {
            throw new AccountException("로그인이 되어있지 않습니다.");
        }

        String userName = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findByEmail(userName)
                .orElseThrow(() -> new UserException(NOT_FOUNT_USER));

        UserDetail userDetail = UserDetail.createUserDetail(userDetailRequest);
        user.setUserDetail(userDetail);

        return MessageResponse.of(REQUEST_SUCCESS);
    }


}
