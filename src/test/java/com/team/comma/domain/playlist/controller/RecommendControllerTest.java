package com.team.comma.domain.playlist.controller;

import static com.team.comma.domain.playlist.constant.RecommendType.FOLLOWING;
import static com.team.comma.global.common.constant.ResponseCodeEnum.RECOMMEND_ALREADY_EXIST;
import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.team.comma.domain.playlist.controller.RecommendController;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.domain.playlist.domain.Playlist;
import com.team.comma.domain.playlist.constant.RecommendListType;
import com.team.comma.domain.playlist.constant.RecommendType;
import com.team.comma.domain.playlist.domain.Recommend;
import com.team.comma.domain.playlist.dto.recommend.RecommendListRequest;
import com.team.comma.domain.playlist.dto.recommend.RecommendRequest;
import com.team.comma.domain.playlist.dto.recommend.RecommendResponse;
import com.team.comma.domain.playlist.exception.RecommendException;
import com.team.comma.domain.playlist.service.RecommendService;
import com.team.comma.domain.track.domain.Track;
import com.team.comma.domain.user.constant.UserRole;
import com.team.comma.domain.user.constant.UserType;
import com.team.comma.domain.user.domain.User;
import com.team.comma.domain.user.domain.UserDetail;
import com.team.comma.global.gson.GsonUtil;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.List;

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(RecommendController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebAppConfiguration
public class RecommendControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    RecommendService recommendService;

    MockMvc mockMvc;
    Gson gson;

    @BeforeEach
    public void init(WebApplicationContext webApplicationContext,
                     RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();

        gson = GsonUtil.getGsonInstance();
    }

    @Test
    void 추천_저장_성공() throws Exception {
        // given
        final String url = "/recommend";

        final RecommendRequest recommendRequest = buildRequest();
        final MessageResponse messageResponse = MessageResponse.of(REQUEST_SUCCESS);
        doReturn(messageResponse).when(recommendService).addRecommend("accessToken", recommendRequest);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .cookie(new Cookie("accessToken", "accessToken"))
                        .content(gson.toJson(recommendRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("spotify/saveRecommend",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 access token")
                        ),
                        requestFields(
                                fieldWithPath("recommendPlaylistId").description("추천 플레이리스트 id"),
                                fieldWithPath("recommendType").description("추천 대상(익명 = ANONYMOUS, 팔로잉 = FOLLOWING)"),
                                fieldWithPath("recommendToEmail").description("추천 대상 이메일(익명인 경우 입력 X)"),
                                fieldWithPath("comment").description("추천 코멘트")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("데이터")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());

    }

    @Test
    void 추천_저장_실패_사용자에게_이미_추천한_플레이리스트() throws Exception {
        // given
        final String url = "/recommend";

        final RecommendRequest recommendRequest = buildRequest();
        doThrow(new RecommendException("사용자에게 이미 추천한 플레이리스트 입니다."))
                .when(recommendService).addRecommend("accessToken", recommendRequest);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .cookie(new Cookie("accessToken", "accessToken"))
                        .content(gson.toJson(recommendRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("spotify/saveRecommendFailure",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 access token")
                        ),
                        requestFields(
                                fieldWithPath("recommendPlaylistId").description("추천 플레이리스트 id"),
                                fieldWithPath("recommendType").description("추천 대상(익명 = ANONYMOUS, 팔로잉 = FOLLOWING)"),
                                fieldWithPath("recommendToEmail").description("추천 대상 이메일(익명인 경우 입력 X)"),
                                fieldWithPath("comment").description("추천 코멘트")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("데이터")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(RECOMMEND_ALREADY_EXIST.getCode());
        assertThat(result.getMessage()).isEqualTo(RECOMMEND_ALREADY_EXIST.getMessage());
    }

    @Test
    void 추천_리스트_조회_성공() throws Exception {
        // given
        final String url = "/recommend";

        final User toUser = buildUser("toUserEmail");
        final User fromUser = buildUser("fromUserEmail");

        final Track track = buildTrack();
        final Playlist playlist = buildPlaylist();
        playlist.addPlaylistTrack(track);

        final Recommend recommend = buildRecommendToFollowing(FOLLOWING, playlist, fromUser, toUser);
        final List<RecommendResponse> recommendList = List.of(
                RecommendResponse.of(recommend, toUser, 1L),
                RecommendResponse.of(recommend, toUser, 1L));

        final RecommendListRequest recommendListRequest = RecommendListRequest.builder().recommendListType(RecommendListType.RECIEVED).build();

        final MessageResponse message = MessageResponse.of(REQUEST_SUCCESS, recommendList);
        doReturn(message).when(recommendService).getRecommendList("accessToken", recommendListRequest);

        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .cookie(new Cookie("accessToken", "accessToken"))
                        .content(gson.toJson(recommendListRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("spotify/selectRecommendList",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 access token")
                        ),
                        requestFields(
                                fieldWithPath("recommendListType").description("받은 추천 리스트: RECIEVED, 보낸 추천 리스트: SENDED, 익명 추천 리스트: ANONYMOUS")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("데이터"),
                                fieldWithPath("data.[].recommendId").description("recommend ID"),
                                fieldWithPath("data.[].comment").description("추천 코멘트"),
                                fieldWithPath("data.[].userNickname").description("사용자 닉네임"),
                                fieldWithPath("data.[].userProfileImage").description("사용자 프로필 이미지 url"),
                                fieldWithPath("data.[].playlistId").description("플레이리스트 ID"),
                                fieldWithPath("data.[].playlistTitle").description("플레이리스트 제목"),
                                fieldWithPath("data.[].repAlbumImageUrl").description("대표 이미지 URL"),
                                fieldWithPath("data.[].trackCount").description("트랙 갯수"),
                                fieldWithPath("data.[].playCount").description("추천된 플레이리스트 재생 횟수")
                        )
                )
        );

        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat((List<RecommendResponse>) result.getData()).size().isEqualTo(2);
    }

    @Test
    void 추천_정보_조회_성공() throws Exception {
        // given
        final String url = "/recommend/{recommendId}";

        final User toUser = buildUser("toUserEmail");
        final User fromUser = buildUser("fromUserEmail");

        final Playlist playlist = buildPlaylist();
        final Track track = buildTrack();
        playlist.addPlaylistTrack(track);

        final Recommend recommend = buildRecommendToFollowing(FOLLOWING, playlist, fromUser, toUser);
        final RecommendResponse recommendResponse = RecommendResponse.of(recommend, toUser, 0);

        final MessageResponse message = MessageResponse.of(REQUEST_SUCCESS, recommendResponse);
        doReturn(message).when(recommendService).getRecommend(recommendResponse.getRecommendId());

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(url, recommendResponse.getRecommendId())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("spotify/selectRecommend",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("데이터"),
                                fieldWithPath("data.recommendId").description("recommend ID"),
                                fieldWithPath("data.userNickname").description("사용자 닉네임"),
                                fieldWithPath("data.userProfileImage").description("사용자 프로필 이미지 url"),
                                fieldWithPath("data.comment").description("추천 코멘트"),
                                fieldWithPath("data.playlistId").description("플레이리스트 ID"),
                                fieldWithPath("data.playlistTitle").description("플레이리스트 제목"),
                                fieldWithPath("data.repAlbumImageUrl").description("대표 이미지 URL"),
                                fieldWithPath("data.trackCount").description("트랙 갯수"),
                                fieldWithPath("data.playCount").description("추천된 플레이리스트 재생 횟수")
                        )
                )
        );

        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                new TypeToken<MessageResponse<RecommendResponse>>() {}.getType()
        );

        RecommendResponse recommendResult = (RecommendResponse) result.getData();
        assertThat(recommendResult.getRecommendId()).isEqualTo(recommendResponse.getRecommendId());

    }

    private User buildUser(String toUserEmail) {
        return User.builder()
                .id(1L)
                .email(toUserEmail)
                .type(UserType.GENERAL_USER)
                .role(UserRole.USER)
                .userDetail(UserDetail.builder().profileImageUrl("test").build())
                .build();
    }

    private Track buildTrack() {
        return Track.builder()
                .id(1L)
                .trackTitle("test track")
                .build();
    }

    private Playlist buildPlaylist() {
        return Playlist.builder()
                .id(123L)
                .playlistTitle("test playlist")
                .alarmFlag(true)
                .alarmStartTime(LocalTime.now())
                .build();
    }

    private RecommendRequest buildRequest() {
        return RecommendRequest.builder()
                .recommendPlaylistId(1L)
                .recommendType(RecommendType.FOLLOWING)
                .recommendToEmail("toUserEmail")
                .comment("test recommend")
                .build();
    }

    private Recommend buildRecommendToFollowing(RecommendType type, Playlist playlist, User fromUser, User toUser) {
        return Recommend.builder()
                .id(1L)
                .fromUser(fromUser)
                .toUser(toUser)
                .recommendType(type)
                .comment("test recommend")
                .playlist(playlist)
                .playCount(0L)
                .build();
    }
}
