package com.team.comma.domain.user.profile.service;

import com.team.comma.domain.user.profile.dto.UserDetailRequest;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.exception.UserException;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.domain.user.user.service.UserService;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.jwt.service.JwtService;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.security.auth.login.AccountException;
import java.util.Optional;

import static com.team.comma.global.common.constant.ResponseCodeEnum.NOT_FOUNT_USER;
import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @InjectMocks
    ProfileService profileService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;


    @Test
    @DisplayName("사용자 정보 저장 실패 _ 로그인 되어있지 않음")
    void saveUserInformationFail_notExistToken() {
        // given
        UserDetailRequest userDetail = buildUserDetailRequest();
        // when
        Throwable thrown = catchThrowable(
                () -> profileService.createProfile(userDetail, null));
        // then
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("로그인이 되어있지 않습니다.");
    }

    @Test
    @DisplayName("사용자 정보 저장 실패 _ 존재하지 않는 사용자")
    void saveUserInfomationFail_notExistUser() {
        // given
        UserDetailRequest userDetail = buildUserDetailRequest();
        doReturn("accessToken").when(jwtTokenProvider).getUserPk(any(String.class));
        doReturn(Optional.empty()).when(userRepository).findByEmail(any(String.class));

        // when
        Throwable thrown = catchThrowable(
                () -> profileService.createProfile(userDetail, "accessToken"));

        // then
        assertThat(thrown).isInstanceOf(UserException.class).hasMessage(NOT_FOUNT_USER.getMessage());
    }

    @Test
    @DisplayName("사용자 정보 저장 성공")
    void saveUserInfomation() throws AccountException {
        // given
        UserDetailRequest userDetail = buildUserDetailRequest();
        User user = User.buildUser();
        doReturn("accessToken").when(jwtTokenProvider).getUserPk(any(String.class));
        doReturn(Optional.of(user)).when(userRepository).findByEmail(any(String.class));

        // when
        MessageResponse result = profileService.createProfile(userDetail, "accessToken");

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    private UserDetailRequest buildUserDetailRequest() {
        return UserDetailRequest.builder()
                .nickName("name")
                .build();
    }

}
