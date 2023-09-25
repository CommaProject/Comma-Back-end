package com.team.comma.domain.user.user.service;

import com.team.comma.domain.user.history.service.HistoryService;
import com.team.comma.domain.user.user.constant.UserType;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.dto.UserRequest;
import com.team.comma.domain.user.user.dto.UserResponse;
import com.team.comma.domain.user.user.exception.UserException;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.jwt.service.JwtService;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import com.team.comma.global.security.dto.Token;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.security.auth.login.AccountException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.team.comma.global.common.constant.ResponseCodeEnum.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HistoryService historyService;

    private MockHttpServletRequest request; // request mock

    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setup() {
        request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    @DisplayName("일반 사용자가 OAuth2.0 계정에 접근 시 오류")
    void deniedToGeralUserAccessOAuthUser() {
        // given
        UserRequest login = UserRequest.buildUserRequest("userEmail");

        User userEntity = buildOauthUserEntity();
        doReturn(Optional.of(userEntity)).when(userRepository).findUserByEmail("userEmail");

        // when
        Throwable thrown = catchThrowable(() -> userService.login(login , null));

        // then
        assertThat(thrown).isInstanceOf(AccountException.class)
            .hasMessage("일반 사용자는 OAuth 계정으로 로그인할 수 없습니다.");

        // verify
        verify(userRepository, times(1)).findUserByEmail("userEmail");
    }

    @Test
    @DisplayName("사용자 로그인 예외 _ 일치하지 않은 비밀번호")
    void loginException_notEqualPassword() {
        // given
        UserRequest loginRequest = UserRequest.buildUserRequest("userEmail");

        User user = buildUserEntity("userEmail", "password123");
        doReturn(Optional.of(user)).when(userRepository).findUserByEmail(loginRequest.getEmail());

        // when
        Throwable thrown = catchThrowable(() -> userService.login(loginRequest , null));

        // then
        assertThat(thrown).isInstanceOf(UserException.class).hasMessage(NOT_FOUNT_USER.getMessage());

    }

    @Test
    @DisplayName("사용자 로그인 예외 _ 존재하지 않은 사용자")
    void notExistUserLoginExceptionTest() {
        // given
        UserRequest login = UserRequest.buildUserRequest("userEmail");
        doReturn(Optional.empty()).when(userRepository).findUserByEmail(login.getEmail());

        // when
        Throwable thrown = catchThrowable(() -> userService.login(login , null));

        // then
        assertThat(thrown).isInstanceOf(UserException.class).hasMessage(NOT_FOUNT_USER.getMessage());

        // verify
        verify(userRepository, times(1)).findUserByEmail(login.getEmail());
    }

    @Test
    @DisplayName("사용자 로그인 성공")
    void loginUserTest() throws AccountException {
        // given
        User user = buildUserEntity("userEmail", bCryptPasswordEncoder.encode("password"));
        Token token = Token.builder().accessToken("accessToken").refreshToken("refreshToken").build();

        doReturn(Optional.of(user)).when(userRepository).findUserByEmail("userEmail");
        doReturn(token).when(jwtService).createJwtToken(user);

        UserRequest request = UserRequest.buildUserRequest("userEmail" , "password");
        HttpServletResponse responseMock = Mockito.mock(HttpServletResponse.class);

        // when
        final MessageResponse result = userService.login(request , responseMock);

        // then
        assertThat(result.getCode()).isEqualTo(LOGIN_SUCCESS.getCode());
    }

    @Test
    @DisplayName("회원 가입 예외_존재하는 회원")
    void existUserException() {
        // given
        UserRequest userRequest = UserRequest.buildUserRequest("userEmail");

        User user = buildUserEntity("userEmail", "password");
        doReturn(Optional.of(user)).when(userRepository).findUserByEmail(any(String.class));

        // when
        Throwable thrown = catchThrowable(() -> userService.register(userRequest));

        // then
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("이미 존재하는 계정입니다.");

    }

    @Test
    @DisplayName("사용자 회원 가입 성공")
    void registerUser() throws AccountException {
        // given
        UserRequest userRequest = UserRequest.buildUserRequest("userEmail" , "password132132");
        User user = buildUserEntity("userEmail", bCryptPasswordEncoder.encode("password132132"));
        doReturn(user).when(userRepository).save(any(User.class));
        doReturn(Optional.empty()).when(userRepository).findUserByEmail(userRequest.getEmail());

        // when
        MessageResponse message = userService.register(userRequest);

        // then
        UserResponse result = (UserResponse) message.getData();

        assertThat(message.getCode()).isEqualTo(1);
        assertThat(message.getMessage()).isEqualTo("성공적으로 가입되었습니다.");
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(bCryptPasswordEncoder.matches( "password132132" , result.getPassword())).isEqualTo(true);
    }

    @Test
    @DisplayName("AccessToken 쿠키로 사용자 정보 가져오기 실패 _ 존재하지 않은 사용자")
    void getUserInfoByCookieButNotExistendUser() {
        // given
        doReturn("accessToken").when(jwtTokenProvider).getUserPk(any(String.class));
        doReturn(Optional.empty()).when(userRepository).findUserByEmail(any(String.class));

        // when
        Throwable thrown = catchThrowable(() -> userService.getUserInformation("accessToken"));

        // then
        assertThat(thrown).isInstanceOf(UserException.class).hasMessage(NOT_FOUNT_USER.getMessage());
    }

    @Test
    @DisplayName("AccessToken 쿠키로 사용자 정보 가져오기")
    void getUserInfoByCookie() {
        // given
        User user = buildUserEntity("userEmail", "password");

        doReturn("accessToken").when(jwtTokenProvider).getUserPk(any(String.class));
        doReturn(Optional.of(user)).when(userRepository).findUserByEmail(any(String.class));

        // when
        MessageResponse result = userService.getUserInformation("accessToken");

        // then
        assertThat(result).isNotNull();
        assertThat(((UserResponse) result.getData()).getEmail()).isEqualTo("userEmail");
    }

    @Test
    @DisplayName("사용자 이름이나 닉네임으로 사용자 탐색")
    void searchUserByNameAndNickNameTest() throws AccountException {
        // given
        User user = buildUserEntity("userEmail", "password");
        List<User> userList = Arrays.asList(user , user , user);
        doReturn(userList).when(userRepository).findAllUsersByNameAndNickName(any(String.class));

        // when
        MessageResponse result = userService.findAllUsersBySearchWord("name" , "token");

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(((List<UserResponse>) result.getData()).size()).isEqualTo(3);
    }

    @Test
    void modifyUserPassword_Fail_Logout_status() {
        // given

        // when
        Throwable throwable = catchThrowable(() -> userService.modifyUserPassword(null, "change_password"));

        // then
        assertThat(throwable.getMessage()).isEqualTo("로그인이 되어있지 않습니다.");
    }

    @Test
    void modifyUserPassword_success() throws AccountException {
        // given
        String accessToken = "accessToken";
        User user = buildUserEntity("userEmail", "password");
        doReturn(user.getEmail()).when(jwtTokenProvider).getUserPk(accessToken);
        doReturn(Optional.of(user)).when(userRepository).findUserByEmail(user.getEmail());

        // when
        MessageResponse result = userService.modifyUserPassword(accessToken, "change_password");

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
    }

    public User buildOauthUserEntity() {
        return User.builder()
                .id(1L)
                .email("userEmail")
                .type(UserType.OAUTH_USER)
                .password("password")
                .build();
    }

    public User buildUserEntity(String email, String password) {
        return User.builder()
                .id(1L)
                .email(email)
                .type(UserType.GENERAL_USER)
                .password(password)
                .build();
    }

}
