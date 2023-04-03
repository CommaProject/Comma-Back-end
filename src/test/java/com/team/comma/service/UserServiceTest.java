package com.team.comma.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.security.auth.login.AccountException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.team.comma.dto.MessageDTO;
import com.team.comma.dto.RequestUserDTO;
import com.team.comma.entity.UserEntity;
import com.team.comma.entity.UserEntity.UserType;
import com.team.comma.repository.UserRepository;
import com.team.comma.security.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private JwtService jwtService;

	@Spy
	private JwtTokenProvider jwtTokenProvider;

	private String userEmail = "email@naver.com";
	private String userPassword = "password";
	private String userName = "userName";

	private MockHttpServletRequest request; // request mock

	@BeforeEach
	public void setup() {
		request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
	}

	@Test
	@DisplayName("일반 사용자가 OAuth2.0 계정에 접근 시 오류")
	public void deniedToGeralUserAccessOAuthUser() throws AccountException {
		// given
		RequestUserDTO userDTO = getRequestUser();
		UserEntity userEntity = getOauthUserEntity();
		doReturn(userEntity).when(userRepository).findByEmail(userEmail);

		// when
		Throwable thrown = catchThrowable(() -> userService.login(userDTO));

		// then
		assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("일반 사용자는 OAuth 계정으로 로그인할 수 없습니다.");

		// verify
		verify(userRepository, times(1)).findByEmail(userEmail);
	}

	@Test
	@DisplayName("Oauth2.0 로그인 실패 _ 존재하는 일반 사용자")
	public void existGeneralUser() {
		// given
		RequestUserDTO userDTO = getRequestUser();
		UserEntity generalUserEntity = getGeneralUserEntity();
		doReturn(generalUserEntity).when(userRepository).findByEmail(userDTO.getEmail());

		// when
		Throwable thrown = catchThrowable(() -> userService.loginOauth(userDTO));

		// then
		assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("일반 사용자가 이미 존재합니다.");
	}

	@Test
	@DisplayName("OAuth2.0 DB에 없을 때 회원가입 및 로그인")
	public void registerOauthUser() throws AccountException {
		// given
		RequestUserDTO userDTO = getRequestUser();
		UserEntity generalUserEntity = getGeneralUserEntity();
		doReturn(null).when(userRepository).findByEmail(userDTO.getEmail());
		doReturn(generalUserEntity).when(userRepository).save(any(UserEntity.class));

		// when
		MessageDTO result = userService.loginOauth(userDTO);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getCode()).isEqualTo(1);
	}

	@Test
	@DisplayName("OAuth2.0 계정 중복 시 로그인 성공")
	public void loginOauthUser() throws AccountException {
		// given
		RequestUserDTO userDTO = getRequestUser();
		UserEntity userEntity = getOauthUserEntity();
		doReturn(userEntity).when(userRepository).findByEmail(userEmail);

		// when
		MessageDTO result = userService.loginOauth(userDTO);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getCode()).isEqualTo(1);
	}

	public UserEntity getOauthUserEntity() {
		return UserEntity.builder().email(userEmail).userType(UserType.OAuthUser).password(null).build();
	}

	public UserEntity getGeneralUserEntity() {
		return UserEntity.builder().email(userEmail).userType(UserType.GeneralUser).password(null).build();
	}

	public RequestUserDTO getRequestUser() {
		return RequestUserDTO.builder().email(userEmail).password(userPassword).age("20").isLeave(0).sex("femail")
				.name(userName).build();
	}

}
