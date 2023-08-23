package com.team.comma.domain.user.service;

import com.team.comma.domain.user.user.service.UserService;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.domain.user.history.dto.HistoryRequest;
import com.team.comma.domain.user.history.service.HistoryService;
import com.team.comma.domain.user.user.constant.UserRole;
import com.team.comma.domain.user.user.constant.UserType;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.userDetail.UserDetail;
import com.team.comma.domain.user.user.dto.LoginRequest;
import com.team.comma.domain.user.user.dto.RegisterRequest;
import com.team.comma.domain.user.user.dto.UserDetailRequest;
import com.team.comma.domain.user.user.dto.UserResponse;
import com.team.comma.domain.user.user.repository.FavoriteGenreRepository;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.jwt.service.JwtService;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import com.team.comma.global.security.domain.Token;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.security.auth.login.AccountException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
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
    private FavoriteGenreRepository favoriteGenreRepository;

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
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("정보가 올바르지 않습니다.");

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
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("정보가 올바르지 않습니다.");

        // verify
        verify(userRepository, times(1)).findByEmail(login.getEmail());
    }

    @Test
    @DisplayName("사용자 로그인 성공")
    void loginUserTest() throws AccountException {
        // given
        LoginRequest login = getLoginRequest();
        Optional<User> userEntity = getUserEntity();
        doReturn(userEntity).when(userRepository).findByEmail(userEmail);
        doReturn(
            Token.builder().accessToken("accessTokenData").refreshToken("refreshTokenData").build())
            .when(jwtTokenProvider).createAccessToken(userEntity.get().getUsername(),
                userEntity.get().getRole());
        doNothing().when(jwtService).login(any(Token.class));
        HttpServletResponse responseMock = Mockito.mock(HttpServletResponse.class);

        // when
        final ResponseEntity result = userService.login(login , responseMock);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
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
        Token token = Token.builder().accessToken("accessToken").refreshToken("refreshToken").build();
        doReturn(token).when(jwtTokenProvider).createAccessToken(any() , any());

        doNothing().when(jwtService).login(token);
        doReturn("token").when(jwtTokenProvider).getUserPk(any(String.class));

        Optional<User> user = getUserEntity();
        String accessToken = userService.createJwtToken(user.get()).getAccessToken();
        doReturn(Optional.empty()).when(userRepository).findByEmail(any(String.class));

        // when
        Throwable thrown = catchThrowable(() -> userService.getUserByCookie(accessToken));

        // then
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("AccessToken 쿠키로 사용자 정보 가져오기")
    void getUserInfoByCookie() throws AccountException {
        // given
        Token token = Token.builder().accessToken("accessToken").refreshToken("refreshToken").build();
        doReturn(token).when(jwtTokenProvider).createAccessToken(any() , any());

        doNothing().when(jwtService).login(token);
        doReturn("token").when(jwtTokenProvider).getUserPk(any(String.class));

        User user = getUserEntity().get();
        String accessToken = userService.createJwtToken(user).getAccessToken();
        doReturn(getUserEntity()).when(userRepository).findByEmail(any(String.class));

        // when
        MessageResponse result = userService.getUserByCookie(accessToken);

        // then
        assertThat(result).isNotNull();
        assertThat(((UserResponse) result.getData()).getEmail()).isEqualTo(userEmail);
    }

    @Test
    @DisplayName("사용자 정보 저장 실패 _ 로그인 되어있지 않음")
    void saveUserInformationFail_notExistToken() {
        // given
        UserDetailRequest userDetail = getUserDetailRequest();
        // when
        Throwable thrown = catchThrowable(
            () -> userService.createUserInformation(userDetail, null));
        // then
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("로그인이 되어있지 않습니다.");
    }

    @Test
    @DisplayName("사용자 정보 저장 실패 _ 존재하지 않는 사용자")
    void saveUserInfomationFail_notExistUser() {
        // given
        Token token = Token.builder().accessToken("accessToken").refreshToken("refreshToken").build();
        doReturn(token).when(jwtTokenProvider).createAccessToken(any() , any());

        doNothing().when(jwtService).login(token);
        doReturn("token").when(jwtTokenProvider).getUserPk(any(String.class));

        UserDetailRequest userDetail = getUserDetailRequest();
        Optional<User> user = getUserEntity();
        String accessToken = userService.createJwtToken(user.get()).getAccessToken();
        doReturn(Optional.empty()).when(userRepository).findByEmail(any(String.class));

        // when
        Throwable thrown = catchThrowable(
            () -> userService.createUserInformation(userDetail, accessToken));
        // then
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("사용자 정보 저장 성공")
    void saveUserInfomation() throws AccountException {
        // given
        Token token = Token.builder().accessToken("accessToken").refreshToken("refreshToken").build();
        doReturn(token).when(jwtTokenProvider).createAccessToken(any() , any());

        doNothing().when(jwtService).login(token);
        doReturn("token").when(jwtTokenProvider).getUserPk(any(String.class));

        UserDetailRequest userDetail = getUserDetailRequest();
        Optional<User> user = getUserEntity();
        String accessToken = userService.createJwtToken(user.get()).getAccessToken();
        doReturn(user).when(userRepository).findByEmail(any(String.class));

        // when
        MessageResponse result = userService.createUserInformation(userDetail, accessToken);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    @Test
    @DisplayName("사용자 관심 장르 가져오기 실패 _ 찾을 수 없는 사용자")
    void getInterestGenreFail_notFoundUser() {
        // given
        doReturn(Optional.empty()).when(userRepository).findByEmail(any(String.class));
        doReturn("").when(jwtTokenProvider).getUserPk(any(String.class));

        // when
        Throwable thrown = catchThrowable(() -> userService.getFavoriteGenreList("token"));

        // then
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("사용자 관심 장르 가져오기")
    void getInterestGenre() throws AccountException {
        // given
        Optional<User> user = getUserEntity();
        doReturn(user).when(userRepository).findByEmail(any(String.class));
        doReturn("").when(jwtTokenProvider).getUserPk(any(String.class));
        doReturn(Arrays.asList("genre1" , "genre2" , "genre3")).when(favoriteGenreRepository)
                .findByGenreNameList(any(User.class));

        // when
        List<String> result = userService.getFavoriteGenreList("token");

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("사용자 이름이나 닉네임으로 사용자 탐색")
    void searchUserByNameAndNickNameTest() throws AccountException {
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

    private UserDetailRequest getUserDetailRequest() {
        return UserDetailRequest.builder().age(20).sex("female").nickName("name")
            .recommendTime(LocalTime.of(12, 0))
            .artistNames(Arrays.asList("artist1", "artist2", "artist3"))
            .genres(Arrays.asList("genre1", "genre2", "genre3"))
            .build();
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
                .age(0)
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
