package com.team.comma.domain.user.detail.service;

import com.team.comma.domain.user.detail.domain.UserDetail;
import com.team.comma.domain.user.detail.dto.UserDetailRequest;
import com.team.comma.domain.user.detail.repository.UserDetailRepository;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.exception.UserException;
import com.team.comma.domain.user.user.service.UserService;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import com.team.comma.global.s3.service.FileUploadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import javax.security.auth.login.AccountException;

import java.io.IOException;

import static com.team.comma.global.common.constant.ResponseCodeEnum.NOT_FOUNT_USER;
import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class UserDetailServiceTest {

    @InjectMocks
    UserDetailService userDetailService;
    @Mock
    UserDetailRepository userDetailRepository;
    @Mock
    private UserService userService;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("사용자 정보 저장 실패 _ 로그인 되어있지 않음")
    void createProfile_fail_tokenNotFound() {
        // given
        UserDetailRequest userDetail = UserDetailRequest.buildUserDetailRequest();

        // when
        Throwable thrown = catchThrowable(
                () -> userDetailService.createProfile(userDetail, null));

        // then
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("로그인이 되어있지 않습니다.");
    }

    @Test
    @DisplayName("사용자 정보 저장 실패 _ 존재하지 않는 사용자")
    void createProfile_fail_userNotFound() {
        // given
        doReturn("accessToken").when(jwtTokenProvider).getUserPk(any(String.class));
        doThrow(new UserException(NOT_FOUNT_USER)).when(userService).findUserOrThrow(any(String.class));

        UserDetailRequest userDetail = UserDetailRequest.buildUserDetailRequest();

        // when
        Throwable thrown = catchThrowable(
                () -> userDetailService.createProfile(userDetail, "accessToken"));

        // then
        assertThat(thrown).isInstanceOf(UserException.class).hasMessage(NOT_FOUNT_USER.getMessage());
    }

    @Test
    @DisplayName("사용자 정보 저장 성공")
    void createProfile_success() throws AccountException {
        // given
        User user = User.buildUser();
        doReturn("accessToken").when(jwtTokenProvider).getUserPk(any(String.class));
        doReturn(user).when(userService).findUserOrThrow(any(String.class));

        UserDetailRequest request = UserDetailRequest.buildUserDetailRequest();

        // when
        MessageResponse result = userDetailService.createProfile(request, "accessToken");

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

}
