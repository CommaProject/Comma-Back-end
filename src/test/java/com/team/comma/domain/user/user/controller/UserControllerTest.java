package com.team.comma.domain.user.user.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.team.comma.domain.user.user.exception.UserException;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.domain.user.user.constant.UserRole;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.dto.LoginRequest;
import com.team.comma.domain.user.user.dto.RegisterRequest;
import com.team.comma.domain.user.user.dto.UserResponse;
import com.team.comma.domain.user.user.service.UserService;
import com.team.comma.global.gson.GsonUtil;
import com.team.comma.global.jwt.exception.TokenForgeryException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.security.auth.login.AccountException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static com.team.comma.global.common.constant.ResponseCodeEnum.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.cookies.CookieDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebAppConfiguration
class UserControllerTest {

    @MockBean
    private UserService userService;

    MockMvc mockMvc;
    Gson gson;
    private final String userEmail = "email@naver.com";
    private final String userPassword = "password";

    @BeforeEach
    public void init(WebApplicationContext webApplicationContext,
                     RestDocumentationContextProvider restDocumentation) {
        gson = GsonUtil.getGsonInstance();

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("로그인 요청 성공")
    void loginRequestSuccess() throws Exception {
        // given
        String api = "/login";
        LoginRequest request = getLoginRequest();
        UserResponse response = getUserResponse();
        MessageResponse message = MessageResponse.of(LOGIN_SUCCESS, response);
        doReturn(message).when(userService).login(any(LoginRequest.class) , any(HttpServletResponse.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(
                document("user/login",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("아이디"),
                                fieldWithPath("password").description("비밀 번호")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("사용자 데이터"),
                                fieldWithPath("data.email").description("이메일"),
                                fieldWithPath("data.password").description("비밀번호"),
                                fieldWithPath("data.delFlag").description("탈퇴 여부 True -> 탈퇴한 사용자"),
                                fieldWithPath("data.role").description("사용자 권한"),
                                fieldWithPath("data.userId").description("사용자 Id 데이터"),
                                fieldWithPath("data.profileImage").description("사용자 프로필 이미지 URL"),
                                fieldWithPath("data.name").description("사용자 이름"),
                                fieldWithPath("data.joinDate").description("가입 날짜"),
                                fieldWithPath("data.nickName").description("사용자 닉네임")
                        )
                )
        );

        final MessageResponse responseResult = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                new TypeToken<MessageResponse<UserResponse>>() {}.getType());

        UserResponse userResponseResult = (UserResponse) responseResult.getData();
        assertThat(responseResult.getCode()).isEqualTo(LOGIN_SUCCESS.getCode());
        assertThat(responseResult.getMessage()).isEqualTo("로그인이 성공적으로 되었습니다.");
        assertThat(userResponseResult.getEmail()).isEqualTo(request.getEmail());
    }

    @Test
    @DisplayName("로그인 요청 실패 _ 틀린 비밀번호 혹은 아이디")
    void loginRequestFail_notExistUser() throws Exception {
        // given
        String api = "/login";
        LoginRequest request = getLoginRequest();
        doThrow(new AccountException("정보가 올바르지 않습니다.")).when(userService).login(any(LoginRequest.class) , any(HttpServletResponse.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("user/login-Fail/wrongInfo",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("아이디"),
                                fieldWithPath("password").description("비밀 번호")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("데이터")
                        )
                )
        );
        final MessageResponse response = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(response.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE.getCode());
        assertThat(response.getMessage()).isEqualTo("정보가 올바르지 않습니다.");
    }

    @Test
    @DisplayName("사용자 회원가입 성공")
    void registUserSuccess() throws Exception {
        // given
        final String api = "/register";
        LoginRequest request = getLoginRequest();
        UserResponse response = getUserResponse();
        doReturn(MessageResponse.of(REGISTER_SUCCESS, response)).when(userService).register(any(RegisterRequest.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("user/register",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("아이디"),
                                fieldWithPath("password").description("비밀 번호")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("사용자 데이터"),
                                fieldWithPath("data.email").description("이메일"),
                                fieldWithPath("data.password").description("비밀번호"),
                                fieldWithPath("data.delFlag").description("탈퇴 여부 True -> 탈퇴한 사용자"),
                                fieldWithPath("data.role").description("사용자 권한"),
                                fieldWithPath("data.userId").description("사용자 Id 데이터"),
                                fieldWithPath("data.profileImage").description("사용자 프로필 이미지 URL"),
                                fieldWithPath("data.name").description("사용자 이름"),
                                fieldWithPath("data.nickName").description("사용자 닉네임"),
                                fieldWithPath("data.joinDate").description("가입 날짜")
                        )
                )
        );
        final MessageResponse responseResult = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                new TypeToken<MessageResponse<UserResponse>>() {}.getType());

        UserResponse userResponse = (UserResponse) responseResult.getData();
        assertThat(responseResult.getCode()).isEqualTo(REGISTER_SUCCESS.getCode());
        assertThat(responseResult.getMessage()).isEqualTo("성공적으로 가입되었습니다.");
        assertThat(userResponse.getEmail()).isEqualTo(request.getEmail());
    }

    @Test
    @DisplayName("사용자 회원가입 실패 _ 이미 존재하는 회원")
    void registUserFail_existUserException() throws Exception {
        // given
        final String api = "/register";
        LoginRequest request = getLoginRequest();
        doThrow(new AccountException("이미 존재하는 계정입니다.")).when(userService).register(any(RegisterRequest.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("user/register-Fail/existUser",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("아이디"),
                                fieldWithPath("password").description("비밀 번호")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"), //
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("데이터")
                        )
                )
        );
        final MessageResponse response = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(response.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE.getCode());
        assertThat(response.getMessage()).isEqualTo("이미 존재하는 계정입니다.");
    }

    @Test
    @DisplayName("AccessToken 으로 사용자 정보 가져오기 실패 _ 존재하지 않는 회원")
    void getUserInfoByAccessTokenFail_NotExistUser() throws Exception {
        // given
        final String api = "/user/information";
        doThrow(new UserException(NOT_FOUNT_USER)).when(userService).getUserByCookie(any(String.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(api)
                        .cookie(new Cookie("accessToken", "token")));

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("user/getUserInfoByToken-Fail/notExistUser",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("accessToken 명")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("데이터")
                        )
                )
        );
        final MessageResponse response = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(response.getMessage()).isEqualTo(NOT_FOUNT_USER.getMessage());
        assertThat(response.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE.getCode());
    }

    @Test
    @DisplayName("AccessToken 으로 사용자 정보 가져오기 실패 _ AccessToken이 없음")
    void getUserInfoByAccessTokenFail_NotExistToken() throws Exception {
        // given
        final String api = "/user/information";
        doThrow(new TokenForgeryException("알 수 없는 토큰이거나 , 변조되었습니다."))
                .when(userService).getUserByCookie(any(String.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(api)
                        .cookie(new Cookie("accessToken", "token"))
        );

        // then
        resultActions.andExpect(status().isForbidden()).andDo(
                document("user/getUserInfoByToken-Fail/notExistToken",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("accessToken 명")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("데이터")
                        )
                )
        );
        final MessageResponse response = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(response.getMessage()).isEqualTo("알 수 없는 토큰이거나 , 변조되었습니다.");
        assertThat(response.getCode()).isEqualTo(AUTHORIZATION_ERROR.getCode());
    }

    @Test
    @DisplayName("AccessToken 으로 사용자 정보 가져오기")
    void getUserInfoByAccessToken_Success() throws Exception {
        // given
        final String api = "/user/information";
        UserResponse user = UserResponse.builder()
                .email(userEmail)
                .password(userPassword)
                .delFlag(false)
                .role(UserRole.USER)
                .build();
        MessageResponse messageResponse = MessageResponse.of(REQUEST_SUCCESS, user);
        doReturn(messageResponse).when(userService).getUserByCookie(any(String.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(api)
                        .cookie(new Cookie("accessToken", "token"))
        );

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("user/getUserInfoByToken",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("accessToken 명")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("사용자 데이터"),
                                fieldWithPath("data.email").description("이메일"),
                                fieldWithPath("data.password").description("비밀번호"),
                                fieldWithPath("data.delFlag").description("탈퇴 여부 True -> 탈퇴한 사용자"),
                                fieldWithPath("data.role").description("사용자 권한"),
                                fieldWithPath("data.userId").description("사용자 Id 데이터"),
                                fieldWithPath("data.profileImage").description("사용자 프로필 이미지 URL"),
                                fieldWithPath("data.name").description("사용자 이름"),
                                fieldWithPath("data.nickName").description("사용자 닉네임"),
                                fieldWithPath("data.joinDate").description("가입 날짜")
                        )
                )
        );
        final MessageResponse response = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
    }

    @Test
    @DisplayName("이름이나 닉네임으로 사용자 정보 탐색")
    void searchUserByNameAndNickName() throws Exception {
        // given
        String api = "/user/{name}";
        MessageResponse messageResponse = MessageResponse.of(REQUEST_SUCCESS
                , Arrays.asList(getUserResponse(), getUserResponse(), getUserResponse()));
        doReturn(messageResponse).when(userService).searchUserByNameAndNickName("name", "token");

        // when
        final ResultActions resultActions = mockMvc.perform(
                        get(api, "name")
                        .cookie(new Cookie("accessToken", "token"))
        );

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("user/searchUser",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("accessToken 명")
                        ),
                        pathParameters(
                                parameterWithName("name").description("탐색할 사용자 명")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data[]").description("사용자 데이터"),
                                fieldWithPath("data[].email").description("이메일"),
                                fieldWithPath("data[].password").description("비밀번호"),
                                fieldWithPath("data[].delFlag").description("탈퇴 여부 True -> 탈퇴한 사용자"),
                                fieldWithPath("data[].role").description("사용자 권한"),
                                fieldWithPath("data[].userId").description("사용자 Id 데이터"),
                                fieldWithPath("data[].profileImage").description("사용자 프로필 이미지 URL"),
                                fieldWithPath("data[].name").description("사용자 이름"),
                                fieldWithPath("data[].nickName").description("사용자 닉네임"),
                                fieldWithPath("data[].joinDate").description("가입 날짜")
                        )
                )
        );
        final MessageResponse response = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(((List<UserResponse>) response.getData()).size()).isEqualTo(3);
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

    private UserResponse getUserResponse() {
        return UserResponse.builder()
                .email(userEmail)
                .password(userPassword)
                .role(UserRole.USER)
                .delFlag(false)
                .profileImage("s3 Image URL")
                .userId(0)
                .build();
    }

}