package com.team.comma.domain.playlist.playlist.controller;

import static com.team.comma.global.common.constant.ResponseCodeEnum.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.playlist.dto.PlaylistRequest;
import com.team.comma.domain.playlist.playlist.dto.PlaylistResponse;
import com.team.comma.domain.playlist.playlist.dto.PlaylistUpdateRequest;
import com.team.comma.domain.playlist.playlist.exception.PlaylistException;
import com.team.comma.domain.playlist.playlist.service.PlaylistService;
import com.team.comma.domain.playlist.track.service.PlaylistTrackService;
import com.team.comma.global.gson.GsonUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(PlaylistController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebAppConfiguration
class PlaylistControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PlaylistService playlistService;

    @MockBean
    PlaylistTrackService playlistTrackService;

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
    void 플레이리스트_조회_성공() throws Exception {
        // given
        final String url = "/playlist";

        final List<PlaylistResponse> playlist = Arrays.asList(
                PlaylistResponse.of(buildPlaylist(),3, "representative album image url"),
                PlaylistResponse.of(buildPlaylist(),3, "representative album image url"));

        final MessageResponse message = MessageResponse.of(REQUEST_SUCCESS, playlist);

        doReturn(message).when(playlistService).findAllPlaylists("accessToken");

        // when
        final ResultActions resultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.get(url)
                .cookie(new Cookie("accessToken", "accessToken"))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk()).andDo(
            document("spotify/selectPlaylist",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestCookies(
                    cookieWithName("accessToken").description("사용자 access token 값")
                ),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메세지"),
                    fieldWithPath("data").description("응답 데이터"),
                    fieldWithPath("data.[].playlistId").description("플레이리스트 id"),
                    fieldWithPath("data.[].playlistTitle").description("플레이리스트 제목"),
                    fieldWithPath("data.[].alarmFlag").description("알람 설정 여부, true = on / false = off"),
                    fieldWithPath("data.[].alarmStartTime").description("알람 시작 시간"),
                    fieldWithPath("data.[].trackCount").description("플레이리스트에 포함된 트랙 갯수"),
                    fieldWithPath("data.[].repAlbumImageUrl").description("플레이리스트 대표 앨범 이미지 url")
                )
            )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat((List<PlaylistResponse>) result.getData()).size().isEqualTo(2);

    }

    @Test
    void 단일_플레이리스트_조회_실패_찾을수없는_플레이리스트() throws Exception {
        // given
        final String url = "/playlist/{playlistId}";

        doThrow(new PlaylistException("PlayList 정보를 찾을 수 없습니다.")).when(playlistService).findPlaylist(30L);

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.get(url , 30)
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("spotify/dummy",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("playlistId").description("플레이리스트 아이디")
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

        assertThat(result.getCode()).isEqualTo(-5);
    }

    @Test
    void 단일_플레이리스트_조회_성공() throws Exception {
        // given
        final String url = "/playlist/{playlistId}";

        final PlaylistResponse playlist = PlaylistResponse.of(buildPlaylist(),3, "representative album image url");
        final MessageResponse message = MessageResponse.of(REQUEST_SUCCESS, playlist);

        doReturn(message).when(playlistService).findPlaylist(30L);

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.get(url , 30)
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("spotify/findPlaylist",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("playlistId").description("플레이리스트 아이디")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터"),
                                fieldWithPath("data.playlistId").description("플레이리스트 id"),
                                fieldWithPath("data.playlistTitle").description("플레이리스트 제목"),
                                fieldWithPath("data.alarmFlag").description("알람 설정 여부, true = on / false = off"),
                                fieldWithPath("data.alarmStartTime").description("알람 시작 시간"),
                                fieldWithPath("data.trackCount").description("플레이리스트에 포함된 트랙 갯수"),
                                fieldWithPath("data.repAlbumImageUrl").description("플레이리스트 대표 앨범 이미지 url")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(1);
    }

    @Test
    void 플레이리스트_알람설정변경_성공() throws Exception {
        // given
        final String url = "/playlist/alert";

        doReturn(MessageResponse.of(PLAYLIST_ALARM_UPDATED)
        ).when(playlistService).modifyPlaylistAlarmFlag(123L, false);

        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.patch(url)
                .content(gson.toJson(
                    PlaylistRequest.builder().playlistId(123L).alarmFlag(false).build()))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk()).andDo(
            document("spotify/updatePlaylistAlert",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("playlistId").description("플레이리스트 id"),
                    fieldWithPath("alarmFlag").description("변경 될 알람 상태, true = on / false = off")
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

        assertThat(result.getCode()).isEqualTo(PLAYLIST_ALARM_UPDATED.getCode());
        assertThat(result.getMessage()).isEqualTo(PLAYLIST_ALARM_UPDATED.getMessage());
    }

    @Test
    void 플레이리스트_알람설정변경_실패_플레이리스트_찾을수없음() throws Exception {
        // given
        final String url = "/playlist/alert";

        doThrow(new PlaylistException("플레이리스트를 찾을 수 없습니다."))
                .when(playlistService).modifyPlaylistAlarmFlag(123L, false);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch(url)
                        .content(gson.toJson(
                                PlaylistRequest.builder().playlistId(123L).alarmFlag(false).build()))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("spotify/updatePlaylistAlertFail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("playlistId").description("플레이리스트 id"),
                                fieldWithPath("alarmFlag").description("변경 될 알람 상태, true = on / false = off")
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

        assertThat(result.getCode()).isEqualTo(PLAYLIST_NOT_FOUND.getCode());
        assertThat(result.getMessage()).isEqualTo(PLAYLIST_NOT_FOUND.getMessage());
    }

    @Test
    void 플레이리스트_삭제_성공() throws Exception {
        // given
        final String url = "/playlist";
        final List<Long> playlistIdList = Arrays.asList(123L, 124L);

        doReturn(MessageResponse.of(PLAYLIST_DELETED)
        ).when(playlistService).modifyPlaylistsDelFlag(playlistIdList);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url)
                        .content(gson.toJson(playlistIdList))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("spotify/deletePlaylist",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("[]").description("플레이리스트 id 리스트")
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

        assertThat(result.getCode()).isEqualTo(PLAYLIST_DELETED.getCode());
        assertThat(result.getMessage()).isEqualTo(PLAYLIST_DELETED.getMessage());
    }

    @Test
    void 플레이리스트_삭제_실패_플레이리스트_찾을수없음() throws Exception {
        // given
        final String url = "/playlist";
        final List<Long> playlistIdList = Arrays.asList(123L, 124L);

        doThrow(new PlaylistException("플레이리스트를 찾을 수 없습니다."))
                .when(playlistService).modifyPlaylistsDelFlag(playlistIdList);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url)
                        .content(gson.toJson(playlistIdList))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("spotify/deletePlaylistFail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("[]").description("플레이리스트 id 리스트")
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

        assertThat(result.getCode()).isEqualTo(PLAYLIST_NOT_FOUND.getCode());
        assertThat(result.getMessage()).isEqualTo(PLAYLIST_NOT_FOUND.getMessage());
    }

    @Test
    void 플레이리스트의_총재생시간_조회_성공() throws Exception {
        //given
        doReturn(
            MessageResponse.of(
                REQUEST_SUCCESS.getCode(),
                REQUEST_SUCCESS.getMessage(),
                1000)
        ).when(playlistService)
            .findTotalDurationTimeMsByPlaylist(anyLong());

        ResultActions resultActions = mockMvc.perform(
            get("/playlist/total-duration-time/{id}", 1L)
        ).andDo(print());

        resultActions.andExpect(status().isOk())
            .andDo(
                document("spotify/selectPlaylistTotalDurationTime",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("id").description("플레이리스트 id")
                    ),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("메세지"),
                        fieldWithPath("data").description("총재생시간(ms)")
                    )
                ));
    }

    //    MethodArgumentTypeMismatchException
    @Test
    void 잘못된타입_들어오면_플레이리스트의_총재생시간_조회_실패() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                get("/playlist/total-duration-time/{id}", "wrongType")
            )
            .andDo(print());

        resultActions.andExpect(status().isBadRequest())
            .andDo(
                document(
                    "spotify/selectPlaylistTotalDurationTimeFail",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("id").description("플레이리스트 id")
                    ),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("메세지"),
                        fieldWithPath("data").description("데이터")
                    )
                )
            );
    }

    @Test
    void 플레이리스트_수정_성공() throws Exception {
        //given
        doReturn(
            MessageResponse.of
                (
                    REQUEST_SUCCESS.getCode(),
                    REQUEST_SUCCESS.getMessage()
                ))
            .when(playlistService).modifyPlaylist(any(PlaylistUpdateRequest.class));

        //when
        ResultActions resultActions = mockMvc.perform(
            patch("/playlist")
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(PlaylistUpdateRequest.builder()
                    .id(1L)
                    .playlistTitle("test playlist")
                    .alarmStartTime(LocalTime.of(10, 10))
                    .build()))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk())
            .andDo(
                document(
                    "spotify/updatePlaylist",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("id").description("플레이리스트 id"),
                        fieldWithPath("playlistTitle").description("플레이리스트 제목"),
                        fieldWithPath("alarmStartTime").description("알람 시작 시간"),
                        fieldWithPath("listSequence").ignored()
                    ),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("메세지"),
                        fieldWithPath("data").ignored()
                    )

                )
            );

    }

    @Test
    void 플레이리스트_수정_실패() throws Exception {
        //given
        doThrow(
            new EntityNotFoundException("해당 플레이리스트가 없습니다")
        ).when(playlistService).modifyPlaylist(any(PlaylistUpdateRequest.class));

        //when
        ResultActions resultActions = mockMvc.perform(
            patch("/playlist")
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(PlaylistUpdateRequest.builder()
                    .id(1L)
                    .playlistTitle("test playlist")
                    .alarmStartTime(LocalTime.of(10, 10))
                    .build()))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isBadRequest())
            .andDo(
                document(
                    "spotify/updatePlaylistFail",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("id").description("플레이리스트 id"),
                        fieldWithPath("playlistTitle").description("플레이리스트 제목"),
                        fieldWithPath("alarmStartTime").description("알람 시작 시간"),
                        fieldWithPath("listSequence").ignored()
                    ),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("메세지"),
                        fieldWithPath("data").ignored()
                    )

                )
            );

    }

    private Playlist buildPlaylist() {
        return Playlist.builder()
            .id(123L)
            .playlistTitle("test playlist")
            .alarmFlag(true)
            .alarmStartTime(LocalTime.now())
            .build();
    }
}
