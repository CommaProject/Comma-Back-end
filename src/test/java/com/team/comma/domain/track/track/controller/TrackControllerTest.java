package com.team.comma.domain.track.track.controller;

import com.google.gson.Gson;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.artist.domain.TrackArtist;
import com.team.comma.domain.track.track.service.TrackService;
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
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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
    @DisplayName("유저가 좋아요 누른 트랙 탐색")
    public void findTrackByFavoriteTrack() throws Exception {
        // given
        final String url = "/tracks/users/favorites";
        List<Track> tracks = new ArrayList<>();
        for(int i = 0; i < 2; i++) {
            tracks.add(buildTrack("title" , "spotifyId"));
        }

        doReturn(MessageResponse.of(REQUEST_SUCCESS , tracks)).when(trackService).findTrackByFavoriteTrack(any(String.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .cookie(new Cookie("accessToken", "accessToken"))
        );

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("track/favoriteTrack",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 access token")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("데이터"),
                                fieldWithPath("data.[].id").description("트랙 테이블 ID"),
                                fieldWithPath("data.[].trackTitle").description("트랙 이름"),
                                fieldWithPath("data.[].durationTimeMs").description("트랙 재생 시간"),
                                fieldWithPath("data.[].recommendCount").description("트랙 추천 횟수"),
                                fieldWithPath("data.[].albumImageUrl").description("트랙 이미지 주소"),
                                fieldWithPath("data.[].spotifyTrackId").description("스포티파이 트랙 ID"),
                                fieldWithPath("data.[].spotifyTrackHref").description("아티스트 트랙 재생 주소"),
                                fieldWithPath("data.[].trackArtistList").description("아티스트 목록"),
                                fieldWithPath("data.[].trackArtistList[].id").description("제공되지 않는 데이터입니다."),
                                fieldWithPath("data.[].trackArtistList[].artistName").description("아티스트 명"),
                                fieldWithPath("data.[].trackArtistList[].track").description("제공되지 않는 데이터입니다.")
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
                .trackArtistList(Arrays.asList(buildTrackArtist()))
                .build();
    }

    public TrackArtist buildTrackArtist() {
        return TrackArtist.builder()
                .artistName("artist")
                .build();
    }

}
