package com.team.comma.spotify.playlist.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.dto.PlaylistResponse;
import com.team.comma.spotify.playlist.dto.PlaylistTrackArtistResponse;
import com.team.comma.spotify.playlist.dto.PlaylistTrackResponse;
import com.team.comma.spotify.playlist.dto.PlaylistRequest;
import com.team.comma.spotify.playlist.service.PlaylistService;
import com.team.comma.spotify.track.domain.Track;
import com.team.comma.spotify.track.domain.TrackArtist;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
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

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(PlaylistController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebAppConfiguration
class PlaylistControllerTest {

    @MockBean
    PlaylistService playlistService;

    MockMvc mockMvc;
    Gson gson;
    private String userEmail = "email@naver.com";

    @BeforeEach
    public void init(WebApplicationContext webApplicationContext,
                     RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();

        gson = new Gson();
    }

    @Test
    public void 플레이리스트_조회_실패_사용자이메일없음() throws  Exception {
        // given
        final String url = "/playlist";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
        );

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("spotify/ㄴ색ㄷ",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("email").description("이메일")
                        ),
                        responseFields(
                                fieldWithPath("[].playlistId").description("플레이리스트 식별자"),
                                fieldWithPath("[].playlistTitle").description("플레이리스트 제목"),
                                fieldWithPath("[].alarmFlag").description("알람 설정 여부, true = on / false = off"),
                                fieldWithPath("[].alarmStartTime").description("알람 시작 시간"),

                                fieldWithPath("[].trackList.[].trackId").description("트랙 식별자"),
                                fieldWithPath("[].trackList.[].trackTitle").description("트랙 제목"),
                                fieldWithPath("[].trackList.[].durationTimeMs").description("재생시간"),
                                fieldWithPath("[].trackList.[].albumImageUrl").description("앨범 이미지 URL"),
                                fieldWithPath("[].trackList.[].trackAlarmFlag").description("알람 설정 여부, 플레이리스트 알람 설정과 관계 없이 개별로 설정 가능"),

                                fieldWithPath("[].trackList.[].trackArtistList.[].artistId").description("가수 식별자"),
                                fieldWithPath("[].trackList.[].trackArtistList.[].artistName").description("가수 이름")
                        )
                )
        );
    }

    @Test
    public void 플레이리스트_조회_성공() throws Exception {
        // given
        final String url = "/playlist";

        final List<PlaylistTrackArtistResponse> trackArtistList = Arrays.asList(
                PlaylistTrackArtistResponse.of(TrackArtist.builder().build()),
                PlaylistTrackArtistResponse.of(TrackArtist.builder().build()));

        final List<PlaylistTrackResponse> trackList = Arrays.asList(
                PlaylistTrackResponse.of(Track.builder().build(), true, trackArtistList),
                PlaylistTrackResponse.of(Track.builder().build(), true, trackArtistList),
                PlaylistTrackResponse.of(Track.builder().build(), true, trackArtistList));

        doReturn(Arrays.asList(
                PlaylistResponse.of(Playlist.builder().build(), trackList),
                PlaylistResponse.of(Playlist.builder().build(), trackList),
                PlaylistResponse.of(Playlist.builder().build(), trackList)
        )).when(playlistService).getPlaylist(userEmail);

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.get(url).header("email",userEmail));
        final List<PlaylistResponse> result = playlistService.getPlaylist(userEmail);

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("spotify/playlist",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("email").description("이메일")
                        ),
                        responseFields(
                                fieldWithPath("[].playlistId").description("플레이리스트 식별자"),
                                fieldWithPath("[].playlistTitle").description("플레이리스트 제목"),
                                fieldWithPath("[].alarmFlag").description("알람 설정 여부, true = on / false = off"),
                                fieldWithPath("[].alarmStartTime").description("알람 시작 시간"),

                                fieldWithPath("[].trackList.[].trackId").description("트랙 식별자"),
                                fieldWithPath("[].trackList.[].trackTitle").description("트랙 제목"),
                                fieldWithPath("[].trackList.[].durationTimeMs").description("재생시간"),
                                fieldWithPath("[].trackList.[].albumImageUrl").description("앨범 이미지 URL"),
                                fieldWithPath("[].trackList.[].trackAlarmFlag").description("알람 설정 여부, 플레이리스트 알람 설정과 관계 없이 개별로 설정 가능"),

                                fieldWithPath("[].trackList.[].trackArtistList.[].artistId").description("가수 식별자"),
                                fieldWithPath("[].trackList.[].trackArtistList.[].artistName").description("가수 이름")
                        )
                )
        );
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void 플레이리스트_알람설정변경_실패_식별자값없음() throws Exception {
        // given
        final String url = "/playlist/update/alarm";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 플레이리스트_알람설정변경_성공() throws Exception {
        // given
        final String url = "/playlist/update/alarm";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(PlaylistRequest.builder().playlistId(123L).alarmFlag(false).build()))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk());
    }
}
