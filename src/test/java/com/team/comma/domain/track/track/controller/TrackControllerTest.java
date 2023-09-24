package com.team.comma.domain.track.track.controller;

import com.google.gson.Gson;
import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.artist.dto.ArtistResponse;
import com.team.comma.domain.track.artist.domain.TrackArtist;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.artist.dto.TrackArtistResponse;
import com.team.comma.domain.track.track.dto.TrackResponse;
import com.team.comma.domain.track.track.service.TrackService;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.gson.GsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(TrackController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebAppConfiguration
public class TrackControllerTest {

    @MockBean
    TrackService trackService;

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
    @DisplayName("추천 받은 인기 트랙")
    public void findTrackByMostFavorite() throws Exception {
        // given
        final String url = "/tracks/recommend";
        List<Track> tracks = new ArrayList<>();
        for(int i = 0; i < 2; i++) {
            tracks.add(buildTrack("title" , "spotifyId"));
        }

        TrackResponse trackResponse = buildTrackResponse("title" , "spotifyId");
        Artist artist = Artist.createArtist("artistId", "artist");
        ArtistResponse artistResponse = ArtistResponse.createArtistResponse(artist);

        List<TrackArtistResponse> data = new ArrayList<>();
        TrackArtistResponse trackArtistResponse = TrackArtistResponse.of(trackResponse , artistResponse);
        data.add(trackArtistResponse);

        doReturn(MessageResponse.of(REQUEST_SUCCESS , data)).when(trackService).findTrackByMostFavorite();

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
        );

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("track/mostListenTrackByRecommendTrack",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("데이터"),
                                fieldWithPath("data.[].track.id").description("트랙 Id"),
                                fieldWithPath("data.[].track.trackTitle").description("트랙 제목"),
                                fieldWithPath("data.[].track.durationTimeMs").description("트랙 재생 시간"),
                                fieldWithPath("data.[].track.recommendCount").description("트랙 추천 횟수"),
                                fieldWithPath("data.[].track.albumImageUrl").description("트랙 엘범 이미지 URL"),
                                fieldWithPath("data.[].track.spotifyTrackId").description("트랙 스포티파이 Id"),
                                fieldWithPath("data.[].track.spotifyTrackHref").description("트랙 스포티파이 주소"),
                                fieldWithPath("data.[].artists.spotifyArtistId").description("트랙 아티스트 Id"),
                                fieldWithPath("data.[].artists.artistName").description("트랙 아티스트 명")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    private Track buildTrack(String title, String spotifyId) {
        return Track.builder()
                .trackTitle(title)
                .recommendCount(0L)
                .albumImageUrl("url")
                .spotifyTrackHref("spotifyTrackHref")
                .spotifyTrackId(spotifyId)
                .trackArtistList(Arrays.asList(buildTrackArtist(buildTrack() , buildArtist())))
                .build();
    }

    public TrackArtist buildTrackArtist(Track track , Artist artist) {
        return TrackArtist.builder()
                .track(track)
                .artist(artist)
                .build();
    }

    private TrackResponse buildTrackResponse(String title, String spotifyId) {
        return TrackResponse.builder()
                .id(1L)
                .trackTitle(title)
                .recommendCount(0L)
                .albumImageUrl("url")
                .spotifyTrackHref("spotifyTrackHref")
                .spotifyTrackId(spotifyId)
                .build();
    }

    private Track buildTrack() {
        return Track.builder()
                .id(1L)
                .trackTitle("title")
                .recommendCount(0L)
                .albumImageUrl("url")
                .spotifyTrackHref("spotifyTrackHref")
                .spotifyTrackId("spotifyId")
                .build();
    }

    private Artist buildArtist() {
        return Artist.builder()
                .spotifyArtistId("artistId")
                .artistName("artistName")
                .build();
    }

}
