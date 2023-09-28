package com.team.comma.domain.track.playcount.controller;

import com.google.gson.Gson;
import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.artist.dto.ArtistResponse;
import com.team.comma.domain.track.artist.dto.TrackArtistResponse;
import com.team.comma.domain.track.playcount.dto.TrackPlayCountRequest;
import com.team.comma.domain.track.playcount.dto.TrackPlayCountResponse;
import com.team.comma.domain.track.playcount.service.PlayCountService;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.track.dto.TrackResponse;
import com.team.comma.domain.track.track.exception.TrackException;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.gson.GsonUtil;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static com.team.comma.global.common.constant.ResponseCodeEnum.SIMPLE_REQUEST_FAILURE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(PlayCountController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebAppConfiguration
public class PlayCountControllerTest {

    @MockBean
    PlayCountService playCountService;

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
    public void createTrackPlay_Success() throws Exception {
        // given
        final String url = "/track/play-count";
        TrackPlayCountRequest request = TrackPlayCountRequest.of();
        doReturn(MessageResponse.of(REQUEST_SUCCESS)).when(playCountService).createTrackPlay(any(String.class) , any(String.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.post(url)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken", "accessToken"))
        );

        // then
        resultActions.andExpect(status().isCreated()).andDo(
                document("play-count/create-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 access token")
                        ),
                        requestFields(
                                fieldWithPath("spotifyTrackId").description("스포티파이 트랙 ID")
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
    public void findMostListenedTrack_Success() throws Exception {
        // given
        final String url = "/track/play-count";

        Artist artist = Artist.buildArtist();
        Track track = Track.buildTrack();
        ArtistResponse artistResponse = ArtistResponse.of(artist);
        TrackResponse trackResponse = TrackResponse.of(track);
        TrackArtistResponse trackArtistResponse = TrackArtistResponse.of(trackResponse, artistResponse);
        TrackPlayCountResponse trackPlayCount = TrackPlayCountResponse.of(100, trackArtistResponse);

        doReturn(MessageResponse.of(REQUEST_SUCCESS , List.of(trackPlayCount))).when(playCountService).findMostListenedTrack(any(String.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .cookie(new Cookie("accessToken", "accessToken"))
        );

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("play-count/find-most-listened-track-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 access token")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("데이터"),
                                fieldWithPath("data.[].trackArtist.track.id").description("아이디"),
                                fieldWithPath("data.[].trackArtist.track.trackTitle").description("트랙 타이틀"),
                                fieldWithPath("data.[].trackArtist.track.durationTimeMs").description("재생 시간"),
                                fieldWithPath("data.[].trackArtist.track.recommendCount").description("추천 횟수"),
                                fieldWithPath("data.[].trackArtist.track.albumImageUrl").description("엘범 이미지 URL"),
                                fieldWithPath("data.[].trackArtist.track.spotifyTrackId").description("스포티파이 트랙 ID"),
                                fieldWithPath("data.[].trackArtist.track.spotifyTrackHref").description("스포티 파이 트랙 주소"),
                                fieldWithPath("data.[].trackArtist.artist.spotifyArtistId").description("스포티 파이 트랙 주소"),
                                fieldWithPath("data.[].trackArtist.artist.artistName").description("스포티 파이 트랙 주소"),
                                fieldWithPath("data.[].trackArtist.artist.artistImageUrl").description("스포티 파이 트랙 주소"),
                                fieldWithPath("data.[].playCount").description("재생 횟수")
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
    public void findMostListenedTrackByFriend_Success() throws Exception {
        // given
        final String url = "/track/play-count/friends";

        Artist artist = Artist.buildArtist();
        Track track = Track.buildTrack();
        ArtistResponse artistResponse = ArtistResponse.of(artist);
        TrackResponse trackResponse = TrackResponse.of(track);
        TrackArtistResponse trackArtistResponse = TrackArtistResponse.of(trackResponse, artistResponse);
        TrackPlayCountResponse trackPlayCount = TrackPlayCountResponse.of(100, trackArtistResponse);

        doReturn(MessageResponse.of(REQUEST_SUCCESS , List.of(trackPlayCount))).when(playCountService).findMostListenedTrackByFriend(any(String.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .cookie(new Cookie("accessToken", "accessToken"))
        );

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("play-count/find-most-listened-track-friend-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 access token")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("데이터"),
                                fieldWithPath("data.[].trackArtist.track.id").description("아이디"),
                                fieldWithPath("data.[].trackArtist.track.trackTitle").description("트랙 타이틀"),
                                fieldWithPath("data.[].trackArtist.track.durationTimeMs").description("재생 시간"),
                                fieldWithPath("data.[].trackArtist.track.recommendCount").description("추천 횟수"),
                                fieldWithPath("data.[].trackArtist.track.albumImageUrl").description("엘범 이미지 URL"),
                                fieldWithPath("data.[].trackArtist.track.spotifyTrackId").description("스포티파이 트랙 ID"),
                                fieldWithPath("data.[].trackArtist.track.spotifyTrackHref").description("스포티 파이 트랙 주소"),
                                fieldWithPath("data.[].trackArtist.artist.spotifyArtistId").description("스포티 파이 트랙 주소"),
                                fieldWithPath("data.[].trackArtist.artist.artistName").description("스포티 파이 트랙 주소"),
                                fieldWithPath("data.[].trackArtist.artist.artistImageUrl").description("스포티 파이 트랙 주소"),
                                fieldWithPath("data.[].playCount").description("재생 횟수")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

}
