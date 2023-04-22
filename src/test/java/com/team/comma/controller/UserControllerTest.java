package com.team.comma.controller;

import com.google.gson.Gson;
import com.team.comma.constant.UserRole;
import com.team.comma.domain.User;
import com.team.comma.dto.LoginRequest;
import com.team.comma.dto.MessageResponse;
import com.team.comma.dto.RegisterRequest;
import com.team.comma.dto.UserDetailRequest;
import com.team.comma.exception.FalsifyTokenException;
import com.team.comma.exception.GeneralExceptionHandler;
import com.team.comma.service.UserService;
import com.team.comma.util.gson.GsonUtil;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.security.auth.login.AccountException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Arrays;

import static com.team.comma.constant.ResponseCode.*;
import static org.apache.http.cookie.SM.SET_COOKIE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

	@InjectMocks
	UserController userController;

	@Mock
	UserService userService;

	MockMvc mockMvc;
	Gson gson;
	private String userEmail = "email@naver.com";
	private String userPassword = "password";

	@BeforeEach
	public void init() {
		gson = GsonUtil.getGsonInstance();

		mockMvc = MockMvcBuilders.standaloneSetup(userController).setControllerAdvice(new GeneralExceptionHandler()) // GeneralException 사용
				.build();
	}

	@Test
	@DisplayName("로그인 요청 성공")
	public void loginUser() throws Exception {
		// given
		final String api = "/login";
		final LoginRequest request = getLoginRequest();
		final MessageResponse message = MessageResponse.of( LOGIN_SUCCESS ,"로그인이 성공적으로 되었습니다." , request.getEmail());
		final ResponseCookie cookie1 = ResponseCookie.from("accessToken" , "accessTokenData1564").build();
		final ResponseCookie cookie2 = ResponseCookie.from("refreshToken" , "refreshTokenData4567").build();
		doReturn(ResponseEntity.ok().header(SET_COOKIE , cookie1.toString()).header(SET_COOKIE , cookie2.toString())
				.body(message)).when(userService).login(any(LoginRequest.class));

		// when
		final ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.post(api).content(gson.toJson(request)).contentType(MediaType.APPLICATION_JSON));

		// then
		resultActions.andExpect(status().isOk());
		String accessToken = resultActions.andReturn().getResponse().getCookie("accessToken").toString();
		String refreshToken = resultActions.andReturn().getResponse().getCookie("refreshToken").toString();
		assertThat(accessToken).contains("accessTokenData1564");
		assertThat(refreshToken).contains("refreshTokenData4567");

		final MessageResponse response = gson.fromJson(
				resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), MessageResponse.class);
		assertThat(response.getCode()).isEqualTo(1);
		assertThat(response.getMessage()).isEqualTo("로그인이 성공적으로 되었습니다.");
		assertThat(response.getData()).isEqualTo(request.getEmail());
	}

	@Test
	@DisplayName("로그인 요청 실패 _ 틀린 비밀번호 혹은 아이디")
	public void notExistUser() throws Exception {
		// given
		final String api = "/login";
		LoginRequest request = getLoginRequest();
		doThrow(new AccountException("정보가 올바르지 않습니다.")).when(userService).login(any(LoginRequest.class));

		// when
		final ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.post(api).content(gson.toJson(request)).contentType(MediaType.APPLICATION_JSON));

		// then
		resultActions.andExpect(status().isBadRequest());
		final MessageResponse response = gson.fromJson(
				resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), MessageResponse.class);

		assertThat(response.getCode()).isEqualTo(-1);
		assertThat(response.getMessage()).isEqualTo("정보가 올바르지 않습니다.");
	}

	@Test
	@DisplayName("사용자 회원가입 성공")
	public void registUser() throws Exception {
		// given
		final String api = "/register";
		LoginRequest request = getLoginRequest();
		doReturn(MessageResponse.of(REGISTER_SUCCESS , "성공적으로 가입되었습니다.")).when(userService)
				.register(any(RegisterRequest.class));

		// when
		final ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.post(api).content(gson.toJson(request)).contentType(MediaType.APPLICATION_JSON));

		// then
		resultActions.andExpect(status().isOk());
		final MessageResponse response = gson.fromJson(
				resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), MessageResponse.class);
		
		assertThat(response.getCode()).isEqualTo(1);
		assertThat(response.getMessage()).isEqualTo("성공적으로 가입되었습니다.");
	}
	
	@Test
	@DisplayName("사용자 회원가입 실패 _ 이미 존재하는 회원")
	public void existUserException() throws Exception {
		// given
		final String api = "/register";
		LoginRequest request = getLoginRequest();
		doThrow(new AccountException("이미 존재하는 계정입니다.")).when(userService)
				.register(any(RegisterRequest.class));

		// when
		final ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.post(api).content(gson.toJson(request)).contentType(MediaType.APPLICATION_JSON));

		// then
		resultActions.andExpect(status().isBadRequest());
		final MessageResponse response = gson.fromJson(
				resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), MessageResponse.class);

		assertThat(response.getCode()).isEqualTo(-1);
		assertThat(response.getMessage()).isEqualTo("이미 존재하는 계정입니다.");
	}

	@Test
	@DisplayName("사용자 정보 저장하기 실패 _ 로그인 되지 않는 사용자")
	public void createUserInformationFail_notExistToken() throws Exception {
		// given
		String api = "/private-information";
		UserDetailRequest userDetail = getUserDetailRequest();
		doThrow(new AccountException("로그인이 되어있지 않습니다.")).when(userService)
				.createUserInformation(any(UserDetailRequest.class) , eq(null));
		// when
		final ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.post(api).content(gson.toJson(userDetail)).contentType(MediaType.APPLICATION_JSON));
		// then
		resultActions.andExpect(status().isBadRequest());
		final MessageResponse response = gson.fromJson(
				resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), MessageResponse.class);

		assertThat(response.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE);
		assertThat(response.getMessage()).isEqualTo("로그인이 되어있지 않습니다.");
	}

	@Test
	@DisplayName("사용자 정보 저장하기 실패 _ 사용자를 찾을 수 없음")
	public void createUserInformationFail_notExistUser() throws Exception {
		// given
		String api = "/private-information";
		UserDetailRequest userDetail = getUserDetailRequest();
		doThrow(new AccountException("사용자를 찾을 수 없습니다.")).when(userService)
				.createUserInformation(any(UserDetailRequest.class) , eq("token"));
		// when
		final ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.post(api).cookie(new Cookie("accessToken" , "token"))
						.content(gson.toJson(userDetail)).contentType(MediaType.APPLICATION_JSON));
		// then
		resultActions.andExpect(status().isBadRequest());
		final MessageResponse response = gson.fromJson(
				resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), MessageResponse.class);

		assertThat(response.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE);
		assertThat(response.getMessage()).isEqualTo("사용자를 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("사용자 정보 저장하기")
	public void createUserInformation() throws Exception {
		// given
		String api = "/private-information";
		UserDetailRequest userDetail = getUserDetailRequest();
		doReturn(ResponseEntity.status(HttpStatus.CREATED).build()).when(userService)
				.createUserInformation(any(UserDetailRequest.class) , eq("token"));
		// when
		final ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.post(api).cookie(new Cookie("accessToken" , "token"))
						.content(gson.toJson(userDetail)).contentType(MediaType.APPLICATION_JSON));
		// then
		resultActions.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("AccessToken 으로 사용자 정보 가져오기 실패 _ 존재하지 않는 회원")
	public void getUserInfoByAccessTokenFail_NotExistUser() throws Exception {
		// given
		final String api = "/user/information";
		doThrow(new AccountException("사용자를 찾을 수 없습니다.")).when(userService).getUserByCookie(any(String.class));
		// when
		final ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.get(api).cookie(new Cookie("accessToken" , "token"))
		);
		// then
		resultActions.andExpect(status().isBadRequest());
		final MessageResponse response = gson.fromJson(
				resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), MessageResponse.class);

		assertThat(response.getMessage()).isEqualTo("사용자를 찾을 수 없습니다.");
		assertThat(response.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE);
	}

	@Test
	@DisplayName("AccessToken 으로 사용자 정보 가져오기 실패 _ AccessToken이 없음")
	public void getUserInfoByAccessTokenFail_NotExistToken() throws Exception {
		// given
		final String api = "/user/information";
		doThrow(new FalsifyTokenException("알 수 없는 토큰이거나 , 변조되었습니다.")).when(userService).getUserByCookie(any(String.class));
		// when
		final ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.get(api).cookie(new Cookie("accessToken" , "token"))
		);
		// then
		resultActions.andExpect(status().isBadRequest());
		final MessageResponse response = gson.fromJson(
				resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), MessageResponse.class);

		assertThat(response.getMessage()).isEqualTo("알 수 없는 토큰이거나 , 변조되었습니다.");
		assertThat(response.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE);
	}

	@Test
	@DisplayName("AccessToken 으로 사용자 정보 가져오기")
	public void getUserInfoByAccessToken_Success() throws Exception {
		// given
		final String api = "/user/information";
		User user = getUserEntity();
		doReturn(user).when(userService).getUserByCookie(any(String.class));
		// when
		final ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.get(api).cookie(new Cookie("accessToken" , "token"))
		);
		// then
		resultActions.andExpect(status().isOk());
		final User response = gson.fromJson(
				resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), User.class);

		assertThat(response).isNotNull();
		assertThat(response.getEmail()).isEqualTo(user.getEmail());
	}


	
	public LoginRequest getLoginRequest() {
		return LoginRequest.builder()
				.email(userEmail)
				.password(userPassword)
				.build();
	}

	private User getUserEntity() {
		return User.builder().email(userEmail).password(userPassword)
				.role(UserRole.USER).build();
	}

	private UserDetailRequest getUserDetailRequest() {
		return UserDetailRequest.builder().age("20").sex("female").nickName("name")
				.recommendTime(LocalTime.of(12 , 0))
				.artistNames(Arrays.asList("artist1" , "artist2" , "artist3"))
				.genres(Arrays.asList("genre1" , "genre2" , "genre3"))
				.build();
	}

}