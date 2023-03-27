package com.team.comma.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.Collections;

import javax.security.auth.login.AccountException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team.comma.dto.MessageDTO;
import com.team.comma.dto.RequestUserDTO;
import com.team.comma.dto.TokenDTO;
import com.team.comma.entity.Token;
import com.team.comma.entity.UserEntity;
import com.team.comma.repository.UserRepository;
import com.team.comma.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@InjectMocks
	UserService userService;
	@Mock
	UserRepository userRepository;
	@Mock
	JwtService jwtService;
	@Mock
	JwtTokenProvider jwtTokenProvider;
	
	private HttpServletResponse response = mock(HttpServletResponse.class);
	private String userEmail = "email@naver.com";
	private String userPassword = "password";
	private String userName = "name";

	@Test
	@DisplayName("사용자 로그인")
	public void userLoginTest() throws AccountException {
		// given
		RequestUserDTO requestDTO = requestUserDTO();
		UserEntity userEntity = userEntity();
		doReturn(userEntity).when(userRepository).findByEmail(userEmail);
		doReturn(Token.builder().build()).when(jwtTokenProvider).createAccessToken(userEntity.getUsername() , userEntity.getRoles());
		doNothing().when(jwtService).login(any(Token.class));
		
		// when
		final TokenDTO result = userService.login(requestDTO, response);

		// then
		assertThat(result.getCode()).isEqualTo(1);
		assertThat(result.getId()).isEqualTo(requestDTO.getEmail());
	}
	
	@Test
	@DisplayName("사용자 로그인 예외_존재하지 않은 사용자")
	public void notExistUserLoginExceptionTest() {
		// given
		RequestUserDTO requestUserDTO = requestUserDTO();
		doReturn(null).when(userRepository).findByEmail(requestUserDTO.getEmail());
		
		// when
		Throwable thrown = catchThrowable(() -> userService.login(requestUserDTO, response)); ;
		
		// then
		assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("정보가 올바르지 않습니다.");
		
		// verify
		verify(userRepository , times(1)).findByEmail(requestUserDTO.getEmail());
	}
	
	@Test
	@DisplayName("사용자 회원 가입")
	public void registUser() throws AccountException {
		// given
		RequestUserDTO requestUserDTO = requestUserDTO();
		doReturn(null).when(userRepository).findByEmail(requestUserDTO.getEmail());
		doReturn(null).when(userRepository).save(any(UserEntity.class));
		
		// when
		MessageDTO messageDTO = userService.register(requestUserDTO);
		
		// then
		assertThat(messageDTO.getCode()).isEqualTo(1);
		assertThat(messageDTO.getMessage()).isEqualTo("성공적으로 가입되었습니다.");
	}
	
	@Test
	@DisplayName("회원 가입 예외_존재하는 회원")
	public void existUserException() {
		// given
		RequestUserDTO requestUserDTO = requestUserDTO();
		doReturn(userEntity()).when(userRepository).findByEmail(requestUserDTO.getEmail());
		
		// when
		Throwable thrown = catchThrowable(() -> userService.register(requestUserDTO));
		
		// then
		assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("이미 존재하는 계정입니다.");
		
	}

	private UserEntity userEntity() {
		return UserEntity.builder().email(userEmail).password(userPassword).name(userName).sex("femail").isLeave(0).roles(Collections.singletonList("ROLE_USER"))
				.recommandTime(LocalDateTime.of(2015, 12, 25, 12, 0)).age("20").build();
	}
	
	private RequestUserDTO requestUserDTO() {
		return RequestUserDTO.builder().age("20").sex("female")
				.recommandTime(LocalDateTime.of(2015, 12, 25, 12, 0)).isLeave(0).email(userEmail).name(userName)
				.password(userPassword).build();
	}

}
