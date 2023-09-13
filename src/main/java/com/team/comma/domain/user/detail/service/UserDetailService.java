package com.team.comma.domain.user.detail.service;

import com.team.comma.domain.user.detail.domain.UserDetail;
import com.team.comma.domain.user.detail.dto.UserDetailRequest;
import com.team.comma.domain.user.detail.repository.UserDetailRepository;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.service.UserService;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import com.team.comma.global.s3.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.login.AccountException;

import java.io.IOException;

import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

@Service
@RequiredArgsConstructor
public class UserDetailService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final FileUploadService fileUploadService;

    private final UserDetailRepository userDetailRepository;

    public MessageResponse createProfile(final UserDetailRequest userDetailRequest, final String token)
            throws AccountException {
        if (token == null) {
            throw new AccountException("로그인이 되어있지 않습니다.");
        }

        String userEmail = jwtTokenProvider.getUserPk(token);
        User user = userService.findUserOrThrow(userEmail);
        UserDetail userDetail = userDetailRequest.toUserDetailEntity();
        user.setUserDetail(userDetail);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

}
