package com.team.comma.domain.user.following.controller;

import com.google.gson.Gson;
import com.team.comma.domain.user.following.constant.FollowingType;
import com.team.comma.domain.user.detail.domain.UserDetail;
import com.team.comma.global.message.MessageResponse;
import com.team.comma.domain.user.following.domain.Following;
import com.team.comma.domain.user.following.dto.FollowingRequest;
import com.team.comma.domain.user.following.dto.FollowingResponse;
import com.team.comma.domain.user.following.exception.FollowingException;
import com.team.comma.domain.user.following.service.FollowingService;
import com.team.comma.domain.user.user.constant.UserRole;
import com.team.comma.domain.user.user.domain.User;
import jakarta.servlet.http.Cookie;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.security.auth.login.AccountException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.team.comma.domain.user.following.constant.FollowingType.FOLLOWING;
import static com.team.comma.global.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static com.team.comma.global.constant.ResponseCodeEnum.SIMPLE_REQUEST_FAILURE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(FollowingController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebAppConfiguration
public class FollowingControllerTest {

    @MockBean
    FollowingService followingService;

    MockMvc mockMvc;
    Gson gson;

    @BeforeEach
    public void init(RestDocumentationContextProvider restDocumentation,
                     WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();

        gson = new Gson();
    }

    @Test
    @DisplayName("새로운 Follow 등록 실패 _ 이미 팔로우 중인 사용자")
    public void addNewFollowFail_alreadyFollowedUser() throws Exception {
        // given
        final String api = "/followings";
        FollowingRequest request = FollowingRequest.builder().toUserId(1L).build();
        doThrow(new FollowingException("이미 팔로우중인 사용자입니다.")).when(followingService).addFollow("accessToken" , 1L);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken" , "accessToken")));

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("following/addFail-alreadyFollowUser",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        requestFields(
                                fieldWithPath("toUserId").description("follow할 대상의 Id"),
                                fieldWithPath("followingId").ignored()
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE.getCode());
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("새로운 Follow 등록 실패 _ 사용자를 찾을 수 없음")
    public void addNewFollowFail_notFoundUser() throws Exception {
        // given
        final String api = "/followings";
        FollowingRequest request = FollowingRequest.builder().toUserId(1L).build();
        doThrow(new AccountException("대상 사용자를 찾을 수 없습니다.")).when(followingService).addFollow("accessToken" , 1L);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken" , "accessToken")));

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("following/addFail-notFoundException",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        requestFields(
                                fieldWithPath("toUserId").description("follow할 대상의 Id"),
                                fieldWithPath("followingId").ignored()
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE.getCode());
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("새로운 Follow 등록 실패 _ 차단된 사용자")
    public void addNewFollowFail_isBlockedUser() throws Exception {
        // given
        final String api = "/followings";
        FollowingRequest request = FollowingRequest.builder().toUserId(1L).build();
        doThrow(new FollowingException("차단된 사용자입니다.")).when(followingService).addFollow("accessToken" , 1L);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken" , "accessToken")));

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("following/addFail-isBlockUser",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        requestFields(
                                fieldWithPath("toUserId").description("follow할 대상의 Id"),
                                fieldWithPath("followingId").ignored()
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE.getCode());
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("새로운 Follow 등록 성공")
    public void addNewFollowSuccess() throws Exception {
        // given
        final String api = "/followings";
        FollowingRequest request = FollowingRequest.builder().toUserId(1L).build();
        MessageResponse message = MessageResponse.of(REQUEST_SUCCESS);
        doReturn(message).when(followingService).addFollow("accessToken" , 1L);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken" , "accessToken")));

        // then
        resultActions.andExpect(status().isCreated()).andDo(
                document("following/addSuccess",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        requestFields(
                                fieldWithPath("toUserId").description("follow할 대상의 Id"),
                                fieldWithPath("followingId").ignored()
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("Follow 삭제")
    public void blockFollow() throws Exception {
        // given
        final String api = "/followings";
        FollowingRequest request = FollowingRequest.builder().followingId(1L).build();
        MessageResponse message = MessageResponse.of(REQUEST_SUCCESS);
        doReturn(message).when(followingService).blockFollowedUser(1L);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .delete(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("following/blockSuccess",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("followingId").description("following 관계 id"),
                                fieldWithPath("toUserId").ignored()
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("Follow 삭제 해제")
    public void unblockFollow() throws Exception {
        // given
        final String api = "/followings/unblocks";
        FollowingRequest request = FollowingRequest.builder().followingId(1L).build();
        MessageResponse message = MessageResponse.of(REQUEST_SUCCESS);
        doReturn(message).when(followingService).unblockFollowedUser(1L);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .patch(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("following/unblockSuccess",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("followingId").description("following 관계 id"),
                                fieldWithPath("toUserId").ignored()
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("Follow 여부 _ 참")
    public void isFollow_true() throws Exception {
        // given
        final String api = "/followings/{toUserId}";
        MessageResponse message = MessageResponse.of(REQUEST_SUCCESS , true);
        doReturn(message).when(followingService).isFollowedUser("accessToken" , 1L);

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders
                        .get(api, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken" , "accessToken")));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("following/isFollow-true",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("toUserId").description("대상 아이디 식별자 값")
                        ),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getData()).isEqualTo(true);
    }

    @Test
    @DisplayName("Follow 여부 _ 거짓")
    public void isFollow_false() throws Exception {
        // given
        final String api = "/followings/{toUserId}";
        MessageResponse message = MessageResponse.of(REQUEST_SUCCESS , false);
        doReturn(message).when(followingService).isFollowedUser("accessToken" , 1L);

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders
                        .get(api, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken" , "accessToken")));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("following/isFollow-false",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("toUserId").description("대상 아이디 식별자 값")
                        ),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getData()).isEqualTo(false);
    }

    @Test
    public void 팔로잉_리스트_조회_성공() throws Exception {
        // given
        final String api = "/followings/type/{followingType}";

        final String token = "accessToken";
        final UserDetail userDetail = UserDetail.builder().id(1L).nickname("user").build();
        final UserDetail targetUserDetail = UserDetail.builder().id(1L).nickname("user").build();
        final User user = User.builder().id(1L).role(UserRole.USER).email("user").userDetail(userDetail).build();
        final User targetUser = User.builder().id(2L).role(UserRole.USER).email("targetUser").userDetail(targetUserDetail).build();
        final Following following = Following.builder().id(1L).blockFlag(false).userFrom(user).userTo(targetUser).build();
        final FollowingResponse response = FollowingResponse.of(following, FOLLOWING);
        final MessageResponse message = MessageResponse.of(REQUEST_SUCCESS , List.of(response,response));

        doReturn(message).when(followingService).getFollowingUserList(token, FOLLOWING);

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.get(api, FOLLOWING)
                        .cookie(new Cookie("accessToken" , token))
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("following/listSuccess",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("followingType").description("팔로우 여부 (FOLLOWING , FOLLOWED , BOTH)")
                        ),
                        requestCookies(
                                cookieWithName("accessToken").description("리스트 조회 할 사용자의 accessToken")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터"),
                                fieldWithPath("data.[].followingId").description("팔로우 관계 ID"),
                                fieldWithPath("data.[].userId").description("사용자 ID"),
                                fieldWithPath("data.[].userEmail").description("사용자 이메일"),
                                fieldWithPath("data.[].userNickname").description("사용자 닉네임"),
                                fieldWithPath("data.[].followForFollow").description("맞팔 여부(0 = 맞팔 아님, 1이상 숫자 = 맞팔 Id")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat((List<FollowingResponse>) result.getData()).size().isEqualTo(2);

    }

    @Test
    public void 팔로잉_팔로워_카운트_조회() throws Exception {
        // given
        final String api = "/followings/count";

        final String token = "accessToken";
        final UserDetail userDetail = UserDetail.builder().id(1L).nickname("user").build();
        final UserDetail targetUserDetail = UserDetail.builder().id(1L).nickname("user").build();
        final User user = User.builder().id(1L).role(UserRole.USER).email("user").userDetail(userDetail).build();
        final User targetUser = User.builder().id(2L).role(UserRole.USER).email("targetUser").userDetail(targetUserDetail).build();
        final Following following = Following.builder().id(1L).blockFlag(false).userFrom(user).userTo(targetUser).build();
        final FollowingResponse followingResponse = FollowingResponse.of(following, FollowingType.FOLLOWING);

        Map<String, Double> response = new HashMap<>();
        response.put("followings", 0.0);
        response.put("followers", 1.0);

        final MessageResponse message = MessageResponse.of(REQUEST_SUCCESS , response);

        doReturn(List.of(followingResponse)).when(followingService).getFollowingFromUserListByToUser(user);
        doReturn(message).when(followingService).countFollowings(token);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(api)
                        .cookie(new Cookie("accessToken" , token))
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("following/count-followings",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("리스트 조회 할 사용자의 accessToken")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터"),
                                fieldWithPath("data.followings").description("팔로잉 수"),
                                fieldWithPath("data.followers").description("팔로워 수")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getData()).isEqualTo(response);

    }

}
