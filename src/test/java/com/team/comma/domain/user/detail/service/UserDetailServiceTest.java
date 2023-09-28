package com.team.comma.domain.user.detail.service;

import com.team.comma.domain.user.detail.domain.UserDetail;
import com.team.comma.domain.user.detail.dto.UserDetailRequest;
import com.team.comma.domain.user.detail.repository.UserDetailRepository;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.exception.UserException;
import com.team.comma.domain.user.user.service.UserService;
import com.team.comma.global.message.MessageResponse;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.security.auth.login.AccountException;

import java.util.Optional;

import static com.team.comma.global.constant.ResponseCodeEnum.NOT_FOUNT_USER;
import static com.team.comma.global.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class UserDetailServiceTest {

    @InjectMocks
    UserDetailService userDetailService;
    @Mock
    UserDetailRepository userDetailRepository;
    @Mock
    private UserService userService;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void createUserDetail_fail_tokenNotFound() {
        // given
        UserDetailRequest userDetail = UserDetailRequest.buildUserDetailCreateRequest();

        // when
        Throwable thrown = catchThrowable(
                () -> userDetailService.createUserDetail(null, userDetail));

        // then
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("로그인이 되어있지 않습니다.");
    }

    @Test
    void createUserDetail_fail_userNotFound() {
        // given
        doReturn("accessToken").when(jwtTokenProvider).getUserPk(anyString());
        doThrow(new UserException(NOT_FOUNT_USER)).when(userService).findUserOrThrow(anyString());

        // when
        Throwable thrown = catchThrowable(
                () -> userDetailService.createUserDetail("accessToken", any(UserDetailRequest.class)));

        // then
        assertThat(thrown).isInstanceOf(UserException.class).hasMessage(NOT_FOUNT_USER.getMessage());
    }

    @Test
    void createUserDetail_success() throws AccountException {
        // given
        String token = "accessToken";
        User user = User.createUser();

        doReturn(user.getEmail()).when(jwtTokenProvider).getUserPk(token);
        doReturn(user).when(userService).findUserOrThrow(user.getEmail());

        UserDetailRequest request = UserDetailRequest.buildUserDetailCreateRequest();

        // when
        MessageResponse result = userDetailService.createUserDetail("accessToken", request);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    @Test
    void findUserDetailOrThrow_success() throws AccountException {
        // given
        User user = User.createUser();
        UserDetail userDetail = UserDetail.buildUserDetail(user);

        doReturn(Optional.of(userDetail)).when(userDetailRepository).findUserDetailByUser(user);

        // when
        UserDetail result = userDetailService.findUserDetailOrThrow(user);

        // then
        assertThat(result).isEqualTo(userDetail);

    }

    @Test
    void modifyUserDetail_success() throws AccountException {
        // given
        String token = "accessToken";
        User user = User.createUser();
        UserDetail userDetail = UserDetail.buildUserDetail(user);

        doReturn(user.getEmail()).when(jwtTokenProvider).getUserPk(token);
        doReturn(user).when(userService).findUserOrThrow(user.getEmail());
        doReturn(Optional.of(userDetail)).when(userDetailRepository).findUserDetailByUser(user);

        UserDetailRequest request = UserDetailRequest.buildUserDetailModifyRequest();

        // when
        MessageResponse result = userDetailService.modifyUserDetail(token, request);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());

    }

}
