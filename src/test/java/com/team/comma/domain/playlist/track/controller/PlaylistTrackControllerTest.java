package com.team.comma.domain.playlist.track.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.playlist.playlist.exception.PlaylistException;
import com.team.comma.domain.playlist.track.dto.PlaylistTrackMultipleRequest;
import com.team.comma.domain.playlist.track.dto.PlaylistTrackRequest;
import com.team.comma.domain.playlist.track.dto.PlaylistTrackResponse;
import com.team.comma.domain.playlist.track.dto.PlaylistTrackSingleRequest;
import com.team.comma.domain.playlist.track.service.PlaylistTrackService;
import com.team.comma.domain.track.artist.domain.TrackArtist;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.track.dto.TrackArtistResponse;
import com.team.comma.global.common.dto.MessageResponse;
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
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static com.team.comma.global.common.constant.ResponseCodeEnum.PLAYLIST_NOT_FOUND;
import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        final PlaylistTrackRequest request = PlaylistTrackRequest.builder()
                .playlistIdList(List.of(1L, 2L, 3L))
                .spotifyTrackId("spotify track id")
                .build();

        doReturn(MessageResponse.of(REQUEST_SUCCESS))
                .when(playlistTrackService).createPlaylistTrack(anyList(), anyString());

        // when
        ResultActions resultActions = mockMvc.perform(
                post(api)
                        .contentType(APPLICATION_JSON)
                        .content(gson.toJson(request)));

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
    void createPlaylistTrack_Fail_PlaylistNotFound() throws Exception {
        //given
        Cookie accessToken = new Cookie("accessToken", "testToken");

        PlaylistTrackRequest playlistTrackRequest = PlaylistTrackRequest.builder()
                .playlistIdList(List.of(1L,2L,3L))
                .spotifyTrackId("spotifyTrackId")
                .build();

        doThrow(new PlaylistException("플레이리스트를 찾을 수 없습니다."))
                .when(playlistTrackService)
                .createPlaylistTrack(anyList(), anyString());

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
    void findPlaylistTrack_Success() throws Exception {
        // given
        final String url = "/playlist/track/{playlistId}";

        final List<TrackArtistResponse> playlistTrackArtistResponseList = List.of(
                TrackArtistResponse.of(buildTrack() , List.of(buildArtist())));
        final List<PlaylistTrackResponse> playlistTracks = Arrays.asList(
                PlaylistTrackResponse.of(true, playlistTrackArtistResponseList),
                PlaylistTrackResponse.of(true, playlistTrackArtistResponseList));

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
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("데이터"),
                                fieldWithPath("data.[].trackAlarmFlag").description("트랙 알람 여부"),
                                fieldWithPath("data.[].trackArtistList[].track.id").description("트랙 Id"),
                                fieldWithPath("data.[].trackArtistList[].track.trackTitle").description("트랙 제목"),
                                fieldWithPath("data.[].trackArtistList[].track.durationTimeMs").description("트랙 재생 시간"),
                                fieldWithPath("data.[].trackArtistList[].track.recommendCount").description("트랙 추천 횟수"),
                                fieldWithPath("data.[].trackArtistList[].track.albumImageUrl").description("트랙 엘범 이미지 URL"),
                                fieldWithPath("data.[].trackArtistList[].track.spotifyTrackId").description("트랙 스포티파이 Id"),
                                fieldWithPath("data.[].trackArtistList[].track.spotifyTrackHref").description("트랙 스포티파이 주소"),
                                fieldWithPath("data.[].trackArtistList[].artist[].id").description("엔티티 식별자"),
                                fieldWithPath("data.[].trackArtistList[].artist[].artistId").description("트랙 아티스트 Id"),
                                fieldWithPath("data.[].trackArtistList[].artist[].artistName").description("트랙 아티스트 명")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat((List<PlaylistTrackResponse>) result.getData()).size().isEqualTo(2);
    }

    @Test
    void findPlaylistTrack_Fail_PlaylistNotFound() throws Exception {
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

    @Test
    void modifyPlaylistTrackSequence_Success() throws Exception {
        // given
        final String api = "/playlist/track/sequence";

        final PlaylistTrackMultipleRequest request = PlaylistTrackMultipleRequest.builder()
                .playlistTrackIdList(List.of(1L, 2L, 3L))
                .build();

        doReturn(MessageResponse.of(REQUEST_SUCCESS))
                .when(playlistTrackService).modifyPlaylistTrackSequence(anyList());

        // when
        ResultActions resultActions = mockMvc.perform(
                patch(api)
                        .contentType(APPLICATION_JSON)
                        .content(gson.toJson(request)));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(
                        document(
                                "playlist/track/modify-sequence-success",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("playlistTrackIdList").description("플레이리스트 트랙 Id 리스트")
                                ),
                                responseFields(
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("message").description("메세지"),
                                        fieldWithPath("data").description("데이터"))
                        )
                );

    }

    @Test
    void modifyPlaylistTrackAlarmFlag_Success() throws Exception {
        // given
        final String api = "/playlist/track/alert";

        final PlaylistTrackSingleRequest request = PlaylistTrackSingleRequest.builder()
                .playlistTrackId(1L)
                .build();

        doReturn(MessageResponse.of(REQUEST_SUCCESS, false))
                .when(playlistTrackService).modifyPlaylistTrackAlarmFlag(anyLong());

        // when
        ResultActions resultActions = mockMvc.perform(
                patch(api)
                        .contentType(APPLICATION_JSON)
                        .content(gson.toJson(request)));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(
                        document(
                                "playlist/track/modify-alarm-flag-success",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("playlistTrackId").description("플레이리스트 트랙 Id")
                                ),
                                responseFields(
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("message").description("메세지"),
                                        fieldWithPath("data").description("데이터 - 변경 된 상태"))
                        )
                );

    }

    @Test
    void deletePlaylistTrack_Success() throws Exception {
        // given
        String api = "/playlist/track";

        PlaylistTrackMultipleRequest request = PlaylistTrackMultipleRequest.builder()
                .playlistTrackIdList(List.of(1L, 2L, 3L))
                .build();

        doReturn(MessageResponse.of(REQUEST_SUCCESS))
                .when(playlistTrackService).deletePlaylistTracks(anyList());

        //when //then
        ResultActions resultActions = mockMvc.perform(
            delete(api)
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(request)));

        resultActions.andExpect(status().isOk())
            .andDo(
                document(
                    "playlist/track/delete-success",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("playlistTrackIdList").description("플레이리스트 트랙 id 리스트")
                    ),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("메세지"),
                        fieldWithPath("data").description("삭제된 트랙 수")
                    )
                )
            );
    }

    private Track buildTrack() {
        return Track.builder()
                .id(123L)
                .trackTitle("test track")
                .durationTimeMs(3000)
                .albumImageUrl("url/test/image")
                .build();
    }

    private TrackArtist buildTrackArtist(Track track , Artist artist){
        return TrackArtist.builder()
                .id(1L)
                .artist(artist)
                .track(track)
                .build();
    }

    private Artist buildArtist() {
        return Artist.builder()
                .artistName("artistName")
                .build();
    }

}