package com.team.comma.domain.user.detail.service;

import com.team.comma.domain.user.detail.domain.UserDetail;
import com.team.comma.domain.user.detail.dto.UserDetailRequest;
import com.team.comma.domain.user.detail.repository.UserDetailRepository;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.service.UserService;
import com.team.comma.global.message.MessageResponse;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountException;

import static com.team.comma.global.constant.ResponseCodeEnum.REQUEST_SUCCESS;

@Service
@RequiredArgsConstructor
public class UserDetailService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    private final UserDetailRepository userDetailRepository;

    @Transactional
    public MessageResponse createUserDetail(final String token, final UserDetailRequest request)
            throws AccountException {
        if (token == null) {
            throw new AccountException("로그인이 되어있지 않습니다.");
        }

        String userEmail = jwtTokenProvider.getUserPk(token);
        User user = userService.findUserOrThrow(userEmail);
        UserDetail userDetail = request.toUserDetailEntity(user);
        userDetailRepository.save(userDetail);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    @Transactional
    public MessageResponse modifyUserDetail(final String token, final UserDetailRequest request)
            throws AccountException {
        if (token == null) {
            throw new AccountException("로그인이 되어있지 않습니다.");
        }

        String userEmail = jwtTokenProvider.getUserPk(token);
        User user = userService.findUserOrThrow(userEmail);
        UserDetail userDetail = findUserDetailOrThrow(user);
        userDetail.modifyUserDetail(request);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    public UserDetail findUserDetailOrThrow(final User user) throws AccountException {
        return userDetailRepository.findUserDetailByUser(user)
                .orElseThrow(() -> new AccountException("등록 된 상세 정보를 찾을 수 없습니다."));
    }

}
