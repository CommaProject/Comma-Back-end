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
import com.team.comma.domain.playlist.playlist.dto.PlaylistModifyRequest;
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
    public void createPlaylist_Success() throws Exception {
        // given
        final String api = "/playlist";

        final PlaylistRequest playlistRequest = PlaylistRequest.builder().spotifyTrackId("spotifyTrackId").build();
        final MessageResponse messageResponse = MessageResponse.of(REQUEST_SUCCESS);
        doReturn(messageResponse).when(playlistService).createPlaylist("accessToken", playlistRequest.getSpotifyTrackId());

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.post(api)
                        .cookie(new Cookie("accessToken", "accessToken"))
                        .content(gson.toJson(playlistRequest))
                        .contentType(APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isCreated()).andDo(
                document("playlist/create-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 access token 값")
                        ),
                        requestFields(
                                fieldWithPath("spotifyTrackId").description("스포티파이 트랙 Id")
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
    }

    @Test
    public void findAllPlaylists_Success() throws Exception {
        // given
        final String url = "/playlist";

        final List<PlaylistResponse> playlist = Arrays.asList(
                PlaylistResponse.of(buildPlaylist(), "representative album image url", 21000L),
                PlaylistResponse.of(buildPlaylist(), "representative album image url", 21000L));

        final MessageResponse message = MessageResponse.of(REQUEST_SUCCESS, playlist);

        doReturn(message).when(playlistService).findAllPlaylists("accessToken");

        // when
        final ResultActions resultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.get(url)
                .cookie(new Cookie("accessToken", "accessToken"))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk()).andDo(
            document("playlist/find-all-success",
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
                    fieldWithPath("data.[].repAlbumImageUrl").description("플레이리스트 대표 앨범 이미지 url"),
                    fieldWithPath("data.[].totalDurationTime").description("플레이리스트 전체 재생 시간")
                )
            )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat((List<PlaylistResponse>) result.getData()).size().isEqualTo(2);

    }

    @Test
    public void findPlaylist_Success() throws Exception {

        // given
        final String url = "/playlist/{playlistId}";

        final PlaylistResponse playlist = PlaylistResponse.of(buildPlaylist(), "representative album image url", 21000L);

        final MessageResponse message = MessageResponse.of(REQUEST_SUCCESS, playlist);

        doReturn(message).when(playlistService).findPlaylist(30L);

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.get(url , 30)
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("playlist/find-success",
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
                                fieldWithPath("data.repAlbumImageUrl").description("플레이리스트 대표 앨범 이미지 url"),
                                fieldWithPath("data.totalDurationTime").description("플레이리스트 전체 재생 시간")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(1);
    }

    @Test
    public void findPlaylist_Fail_PlaylistNotFound() throws Exception {
        // given
        final String url = "/playlist/{playlistId}";

        doThrow(new PlaylistException("PlayList 정보를 찾을 수 없습니다.")).when(playlistService).findPlaylist(30L);

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.get(url , 30)
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("playlist/find-fail-playlist-not-found",
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
    public void findTotalDurationTime_Success() throws Exception {
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
                        document("playlist/find-total-duration-time-success",
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

    @Test
    public void modifyPlaylistTitle_Success() throws Exception {
        //given
        doReturn(MessageResponse.of(REQUEST_SUCCESS))
                .when(playlistService).modifyPlaylistTitle(any(PlaylistModifyRequest.class));

        //when
        ResultActions resultActions = mockMvc.perform(
                patch("/playlist/title")
                        .contentType(APPLICATION_JSON)
                        .content(gson.toJson(PlaylistModifyRequest.builder()
                                .playlistId(1L)
                                .playlistTitle("test playlist")
                                .build()))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andDo(
                        document(
                                "playlist/modify-title-success",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("playlistId").description("플레이리스트 id"),
                                        fieldWithPath("playlistTitle").description("플레이리스트 제목")
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
    public void modifyPlaylistTitle_Fail_PlaylistNotFound() throws Exception {
        //given
        doThrow(
                new EntityNotFoundException("해당 플레이리스트가 없습니다")
        ).when(playlistService).modifyPlaylistTitle(any(PlaylistModifyRequest.class));

        //when
        ResultActions resultActions = mockMvc.perform(
                patch("/playlist/title")
                        .contentType(APPLICATION_JSON)
                        .content(gson.toJson(PlaylistModifyRequest.builder()
                                .playlistId(1L)
                                .playlistTitle("test playlist")
                                .build()))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isBadRequest())
                .andDo(
                        document(
                                "playlist/modify-title-fail-playlist-not-found",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("playlistId").description("플레이리스트 id"),
                                        fieldWithPath("playlistTitle").description("플레이리스트 제목")
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
    public void modifyPlaylistAlarmFlag_Success() throws Exception {
        // given
        final String url = "/playlist/alert";

        final PlaylistModifyRequest request = PlaylistModifyRequest.builder().playlistId(1L).build();

        doReturn(MessageResponse.of(REQUEST_SUCCESS))
                .when(playlistService).modifyPlaylistAlarmFlag(any());

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.patch(url)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("playlist/modify-alarm-flag-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("playlistId").description("플레이리스트 id")
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
    public void modifyPlaylistAlarmFlag_Fail_PlaylistNotFound() throws Exception {
        // given
        final String url = "/playlist/alert";

        final PlaylistModifyRequest request = PlaylistModifyRequest.builder().playlistId(1L).build();

        doThrow(new PlaylistException("플레이리스트를 찾을 수 없습니다."))
                .when(playlistService).modifyPlaylistAlarmFlag(any());

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.patch(url)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("playlist/modify-alarm-flag-fail-playlist-not-found",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("playlistId").description("플레이리스트 id")
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
    public void modifyPlaylistAlarmDayAndTime_Success() throws Exception {
        // given
        final String url = "/playlist/alert/day-time";

        final PlaylistModifyRequest request = PlaylistModifyRequest.builder()
                .playlistId(1L).alarmDays(List.of(1,2,3)).build();

        doReturn(MessageResponse.of(REQUEST_SUCCESS)).when(playlistService).modifyPlaylistAlarmDayAndTime(any());

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.patch(url)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("playlist/modify-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("playlistId").description("플레이리스트 id"),
                                fieldWithPath("alarmDays.[]").description("알람 설정 요일 리스트 (1 ~ 7 : 월 ~ 일)")
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
    public void deletePlaylist_Success() throws Exception {
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
                document("playlist/delete-success",
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
    public void deletePlaylist_Fail_PlaylistNotFound() throws Exception {
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
                document("playlist/delete-fail-playlist-not-found",
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

    private Playlist buildPlaylist() {
        return Playlist.builder()
            .id(123L)
            .playlistTitle("test playlist")
            .alarmFlag(true)
            .alarmStartTime(LocalTime.now())
            .build();
    }
}
