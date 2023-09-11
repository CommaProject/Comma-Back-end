package com.team.comma.domain.user.user.service;

import com.team.comma.domain.user.history.dto.HistoryRequest;
import com.team.comma.domain.user.history.service.HistoryService;
import com.team.comma.domain.user.profile.domain.UserDetail;
import com.team.comma.domain.user.user.constant.UserRole;
import com.team.comma.domain.user.user.constant.UserType;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.dto.LoginRequest;
import com.team.comma.domain.user.user.dto.RegisterRequest;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.security.auth.login.AccountException;
import javax.swing.text.html.Option;
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


    private final String userEmail = "email@naver.com";
    private final String userPassword = "password";

    private MockHttpServletRequest request; // request mock

    @BeforeEach
    void setup() {
        request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    @DisplayName("일반 사용자가 OAuth2.0 계정에 접근 시 오류")
    void deniedToGeralUserAccessOAuthUser() throws AccountException {
        // given
        LoginRequest login = getLoginRequest();
        Optional<User> userEntity = getOauthUserEntity();
        doReturn(userEntity).when(userRepository).findByEmail(userEmail);

        // when
        Throwable thrown = catchThrowable(() -> userService.login(login , null));

        // then
        assertThat(thrown).isInstanceOf(AccountException.class)
            .hasMessage("일반 사용자는 OAuth 계정으로 로그인할 수 없습니다.");

        // verify
        verify(userRepository, times(1)).findByEmail(userEmail);
    }

    @Test
    @DisplayName("사용자 로그인 예외 _ 일치하지 않은 비밀번호")
    void loginException_notEqualPassword() throws AccountException {
        // given
        LoginRequest loginRequest = getLoginRequest();
        User user = User.builder().email(userEmail).password("unknown").role(UserRole.USER)
                .build();
        Optional<User> optionalUser = Optional.of(user);
        doReturn(optionalUser).when(userRepository).findByEmail(loginRequest.getEmail());

        // when
        Throwable thrown = catchThrowable(() -> userService.login(loginRequest , null));

        // then
        assertThat(thrown).isInstanceOf(UserException.class).hasMessage(NOT_FOUNT_USER.getMessage());

    }

    @Test
    @DisplayName("사용자 로그인 예외 _ 존재하지 않은 사용자")
    void notExistUserLoginExceptionTest() {
        // given
        LoginRequest login = getLoginRequest();
        doReturn(Optional.empty()).when(userRepository).findByEmail(login.getEmail());

        // when
        Throwable thrown = catchThrowable(() -> userService.login(login , null));

        // then
        assertThat(thrown).isInstanceOf(UserException.class).hasMessage(NOT_FOUNT_USER.getMessage());

        // verify
        verify(userRepository, times(1)).findByEmail(login.getEmail());
    }

    @Test
    @DisplayName("사용자 로그인 성공")
    void loginUserTest() throws AccountException {
        // given
        Optional<User> user = getUserEntity();
        Token token = Token.builder().accessToken("accessTokenData").refreshToken("refreshTokenData").build();

        doReturn(user).when(userRepository).findByEmail(userEmail);
        doReturn(token).when(jwtService).createJwtToken(user.get());

        LoginRequest request = getLoginRequest();
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
        RegisterRequest registerRequest = getRegisterRequest();
        doReturn(getUserEntity()).when(userRepository).findByEmail(any(String.class));

        // when
        Throwable thrown = catchThrowable(() -> userService.register(registerRequest));

        // then
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("이미 존재하는 계정입니다.");

    }

    @Test
    @DisplayName("사용자 회원 가입 성공")
    void registUser() throws AccountException {
        // given
        RegisterRequest registerRequest = getRegisterRequest();
        Optional<User> userEntity = getUserEntity();
        doReturn(Optional.empty()).when(userRepository).findByEmail(registerRequest.getEmail());
        doReturn(userEntity.get()).when(userRepository).save(any(User.class));

        // when
        MessageResponse message = userService.register(registerRequest);

        // then
        UserResponse user = (UserResponse) message.getData();

        assertThat(message.getCode()).isEqualTo(1);
        assertThat(message.getMessage()).isEqualTo("성공적으로 가입되었습니다.");
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(userEntity.get().getEmail());
    }

    @Test
    @DisplayName("AccessToken 쿠키로 사용자 정보 가져오기 실패 _ 존재하지 않은 사용자")
    void getUserInfoByCookieButNotExistendUser() {
        // given
        doReturn("accessToken").when(jwtTokenProvider).getUserPk(any(String.class));
        doReturn(Optional.empty()).when(userRepository).findByEmail(any(String.class));

        // when
        Throwable thrown = catchThrowable(() -> userService.getUserByCookie("accessToken"));

        // then
        assertThat(thrown).isInstanceOf(UserException.class).hasMessage(NOT_FOUNT_USER.getMessage());
    }

    @Test
    @DisplayName("AccessToken 쿠키로 사용자 정보 가져오기")
    void getUserInfoByCookie() {
        // given
        doReturn("accessToken").when(jwtTokenProvider).getUserPk(any(String.class));
        doReturn(getUserEntity()).when(userRepository).findByEmail(any(String.class));

        // when
        MessageResponse result = userService.getUserByCookie("accessToken");

        // then
        assertThat(result).isNotNull();
        assertThat(((UserResponse) result.getData()).getEmail()).isEqualTo(userEmail);
    }

    @Test
    @DisplayName("사용자 이름이나 닉네임으로 사용자 탐색")
    void searchUserByNameAndNickNameTest() {
        // given
        List<User> userList = Arrays.asList(getUserEntity().get() , getUserEntity().get() , getUserEntity().get());
        doReturn(userList).when(userRepository).searchUserByUserNameAndNickName(any(String.class));
        doReturn(null).when(historyService).addHistory(any(HistoryRequest.class) , any(String.class));

        // when
        MessageResponse result = userService.searchUserByNameAndNickName("name" , "token");

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(((List<UserResponse>) result.getData()).size()).isEqualTo(3);
    }

    private Optional<User> getUserEntity() {
        User user = User.builder().id(0L).email(userEmail).password(userPassword).userDetail(createUserDetail())
                .role(UserRole.USER).build();

        return Optional.of(user);
    }

    private UserDetail createUserDetail() {
        return UserDetail.builder()
                .id(0L)
                .name("name")
                .allPublicFlag(false)
                .calenderPublicFlag(false)
                .favoritePublicFlag(false)
                .nickname("nickName")
                .profileImageUrl("url")
                .build();
    }

    private LoginRequest getLoginRequest() {
        return LoginRequest.builder().email(userEmail).password(userPassword).build();
    }

    private RegisterRequest getRegisterRequest() {
        return RegisterRequest.builder().email(userEmail).password(userPassword).build();
    }

    public Optional<User> getOauthUserEntity() {
        User user = User.builder().id(0L).email(userEmail).type(UserType.OAUTH_USER).password(null).build();

        return Optional.of(user);
    }

}
