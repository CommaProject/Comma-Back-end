package com.team.comma.domain.playlist.track.controller;

import static com.team.comma.global.common.constant.ResponseCodeEnum.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
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
import com.team.comma.domain.track.artist.dto.TrackArtistResponse;
import com.team.comma.domain.playlist.track.dto.PlaylistTrackDeleteRequest;
import com.team.comma.domain.playlist.track.dto.PlaylistTrackResponse;
import com.team.comma.domain.playlist.track.dto.PlaylistTrackRequest;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.domain.playlist.playlist.exception.PlaylistException;
import com.team.comma.domain.playlist.track.service.PlaylistTrackService;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.artist.domain.TrackArtist;
import com.team.comma.global.gson.GsonUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(PlaylistTrackController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebAppConfiguration
class PlaylistTrackControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PlaylistTrackService playlistTrackService;

    MockMvc mockMvc;
    Gson gson;
    private String userEmail = "email@naver.com";

    @BeforeEach
    public void init(WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .build();

        gson = GsonUtil.getGsonInstance();
    }

    @Test
    void createPlaylistTrack_Success() throws Exception {
        // given
        final String api = "/playlist/track";

        final PlaylistTrackRequest playlistTrackRequest = PlaylistTrackRequest.builder()
                .playlistIdList(List.of(1L, 2L, 3L))
                .spotifyTrackId("spotify track id")
                .build();

        doReturn(MessageResponse.of(REQUEST_SUCCESS))
                .when(playlistTrackService).createPlaylistTrack(any());

        // when
        ResultActions resultActions = mockMvc.perform(
                post(api)
                        .contentType(APPLICATION_JSON)
                        .content(gson.toJson(playlistTrackRequest)));

        // then
        resultActions.andExpect(status().isCreated())
                .andDo(
                        document(
                                "playlist/track/create-success",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("playlistIdList.[]").description("플레이리스트 Id 리스트"),
                                        fieldWithPath("spotifyTrackId").description("스포티파이 트랙 Id")
                                ),
                                responseFields(
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("message").description("메세지"),
                                        fieldWithPath("data").description("데이터"))
                        )
                );

    }
    @Test
    void 플레이리스트의_트랙_삭제_성공() throws Exception {
        // given
        Set<Long> trackIdList = Set.of(1L, 2L, 3L);

        doReturn(
            MessageResponse.of(
                REQUEST_SUCCESS.getCode(),
                REQUEST_SUCCESS.getMessage(),
                trackIdList.size())
        ).when(playlistTrackService)
            .removePlaylistAndTrack(anySet(), anyLong());

        //when //then
        ResultActions resultActions = mockMvc.perform(
            delete("/playlist/track")
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(PlaylistTrackDeleteRequest.builder()
                    .trackIdList(trackIdList)
                    .playlistId(1L)
                    .build()))
        ).andDo(print());
        resultActions.andExpect(status().isOk())
            .andDo(
                document(
                    "playlist/track/delete-success",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("playlistId").description("플레이리스트 id"),
                        fieldWithPath("trackIdList").description("삭제할 트랙 id 리스트")
                    ),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("메세지"),
                        fieldWithPath("data").description("삭제된 트랙 수")
                    )
                )
            );
    }

    @Test
    void 플레이리스트의_트랙_삭제_요청_트랙_빈배열요청시_0_리턴() throws Exception {
        //given
        Set<Long> trackIdList = Set.of();
        int trackIdListSize = trackIdList.size();
        doReturn(
            MessageResponse.of(
                REQUEST_SUCCESS.getCode(),
                REQUEST_SUCCESS.getMessage(),
                trackIdListSize)
        ).when(playlistTrackService)
            .removePlaylistAndTrack(anySet(), anyLong());

        //when
        ResultActions resultActions = mockMvc.perform(
            delete("/playlist/track")
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(PlaylistTrackDeleteRequest.builder()
                    .trackIdList(trackIdList)
                    .playlistId(1L)
                    .build()))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void 플레이리스트의_트랙_삭제_요청_플리Id_없으면_실패() throws Exception {
        //given
        Set<Long> trackIdList = Set.of();

        doThrow(new EntityNotFoundException("해당 트랙이 존재하지 않습니다."))
            .when(playlistTrackService).removePlaylistAndTrack(anySet(), anyLong());

        //when
        ResultActions resultActions = mockMvc.perform(
            delete("/playlist/track")
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(PlaylistTrackDeleteRequest.builder()
                    .trackIdList(trackIdList)
                    .playlistId(1L)
                    .build()))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isBadRequest())
            .andDo(
                document(
                    "playlist/track/delete-fail-track-not-found",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("메세지"),
                        fieldWithPath("data").description("데이터").ignored()
                    )
                )
            );
    }

    @Test
    void 플레이리스트트랙_저장_성공() throws Exception {
        Cookie accessToken = new Cookie("accessToken", "testToken");

        MessageResponse messageResponse = MessageResponse.of
            (
                REQUEST_SUCCESS.getCode(),
                REQUEST_SUCCESS.getMessage()
            );

        PlaylistTrackRequest playlistTrackRequest = PlaylistTrackRequest.builder()
            .playlistIdList(List.of(1L,2L,3L))
            .spotifyTrackId("spotifyTrackId")
            .build();

        doReturn(messageResponse)
            .when(playlistTrackService)
            .createPlaylistTrack(any(PlaylistTrackRequest.class));

        //when
        ResultActions resultActions = mockMvc.perform(
            post("/playlist/track")
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(playlistTrackRequest))
                .cookie(accessToken)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isCreated())
            .andDo(
                document(
                    "playlist/track/create-success",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("playlistIdList").description("플레이리스트 ID 리스트, 신규 생성의 경우 입력받지 않음"),
                        fieldWithPath("spotifyTrackId").description("스포티파이 트랙 ID")
                    ),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("메세지"),
                        fieldWithPath("data").description("데이터").ignored()
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
    void 플레이리스트트랙_저장_실패_존재하지않는_플레이리스트() throws Exception {
        //given
        Cookie accessToken = new Cookie("accessToken", "testToken");

        PlaylistTrackRequest playlistTrackRequest = PlaylistTrackRequest.builder()
                .playlistIdList(List.of(1L,2L,3L))
                .spotifyTrackId("spotifyTrackId")
                .build();

        doThrow(new PlaylistException("플레이리스트를 찾을 수 없습니다."))
            .when(playlistTrackService)
            .createPlaylistTrack(any(PlaylistTrackRequest.class));

        //when
        ResultActions resultActions = mockMvc.perform(
            post("/playlist/track")
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(playlistTrackRequest))
                .cookie(accessToken)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isBadRequest())
            .andDo(
                document(
                    "playlist/track/create-fail-playlist-not-found",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("메세지"),
                        fieldWithPath("data").description("데이터").ignored()
                    )
                )
            );

        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(PLAYLIST_NOT_FOUND.getCode());
        assertThat(result.getMessage()).isEqualTo("플레이리스트를 찾을 수 없습니다.");
    }

    @Test
    void 플레이리스트_트랙_상세_리스트_조회_성공() throws Exception {
        // given
        final String url = "/playlist/track/{playlistId}";

        final List<TrackArtistResponse> playlistTrackArtistResponseList = List.of(
                TrackArtistResponse.of(buildTrackArtist()));
        final List<PlaylistTrackResponse> playlistTracks = Arrays.asList(
                PlaylistTrackResponse.of(buildTrack(), true, playlistTrackArtistResponseList),
                PlaylistTrackResponse.of(buildTrack(), true, playlistTrackArtistResponseList));

        final MessageResponse message = MessageResponse.of(REQUEST_SUCCESS, playlistTracks);

        doReturn(message).when(playlistTrackService).findPlaylistTrack(anyLong());

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.get(url,1L));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("playlist/track/find-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("playlistId").description("플레이리스트 id")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터"),
                                fieldWithPath("data.[].trackId").description("트랙 id"),
                                fieldWithPath("data.[].trackTitle").description("트랙 제목"),
                                fieldWithPath("data.[].durationTimeMs").description("트랙 재생 시간"),
                                fieldWithPath("data.[].albumImageUrl").description("트랙 앨범 이미지 URL"),
                                fieldWithPath("data.[].trackAlarmFlag").description("트랙 알람 설정 여부"),
                                fieldWithPath("data.[].trackArtistList").description("트랙 아티스트 리스트"),
                                fieldWithPath("data.[].trackArtistList.[].artistId").description("트랙 아티스트 id"),
                                fieldWithPath("data.[].trackArtistList.[].artistName").description("트랙 아티스트명")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat((List<PlaylistTrackResponse>) result.getData()).size().isEqualTo(2);
    }

    @Test
    void 플레이리스트_트랙_상세_리스트_조회_실패_플레이리스트없음() throws Exception {
        // given
        final String url = "/playlist/track/{playlistId}";

        doThrow(new PlaylistException("플레이리스트를 찾을 수 없습니다."))
                .when(playlistTrackService)
                .findPlaylistTrack(anyLong());

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.get(url,1L));

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("playlist/track/find-fail-playlist-not-found",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("playlistId").description("플레이리스트 id")
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

        assertThat(result.getCode()).isEqualTo(PLAYLIST_NOT_FOUND.getCode());
        assertThat(result.getMessage()).isEqualTo(PLAYLIST_NOT_FOUND.getMessage());
    }

    private Track buildTrack() {
        return Track.builder()
                .id(123L)
                .trackTitle("test track")
                .durationTimeMs(3000)
                .albumImageUrl("url/test/image")
                .build();
    }

    private TrackArtist buildTrackArtist() {
        return TrackArtist.builder()
                .id(123L)
                .artistName("test artist")
                .build();
    }

}