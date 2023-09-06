package com.team.comma.domain.track.playcount.controller;

import com.google.gson.Gson;
import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.track.artist.domain.TrackArtist;
import com.team.comma.domain.track.playcount.dto.TrackPlayCountResponse;
import com.team.comma.domain.track.playcount.service.PlayCountService;
import com.team.comma.domain.track.track.domain.Track;
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
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
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
    @DisplayName("트랙 추천 수 증가")
    public void countPlayCount() throws Exception {
        // given
        final String url = "/tracks/play-count/{trackId}";
        doReturn(MessageResponse.of(REQUEST_SUCCESS)).when(playCountService).modifyPlayCount(any(String.class) , any(String.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.patch(url , "trackId")
                        .cookie(new Cookie("accessToken", "accessToken"))
        );

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("track/addTrackListenedCount",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("trackId").description("스포티파이 트랙 ID")
                        ),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 access token")
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
    @DisplayName("내가 가장 많이 들은 곡")
    public void findMostListenedTrack() throws Exception {
        // given
        final String url = "/tracks";
        List<TrackPlayCountResponse> trackPlayCountResponses = new ArrayList<>();
        for(int i = 0; i < 2; i++) {
            trackPlayCountResponses.add(buildTrackPlayCountResponse(buildTrack()));
        }

        doReturn(MessageResponse.of(REQUEST_SUCCESS , trackPlayCountResponses)).when(playCountService).findMostListenedTrack(any(String.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .cookie(new Cookie("accessToken", "accessToken"))
        );

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("track/mostListenTrackByMe",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 access token")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("데이터"),
                                fieldWithPath("data.[].playCount").description("재생 횟수"),
                                fieldWithPath("data.[].trackId").description("스포티파이 트랙 ID"),
                                fieldWithPath("data.[].trackImageUrl").description("트랙 이미지 URL"),
                                fieldWithPath("data.[].trackName").description("트랙 이름"),
                                fieldWithPath("data.[].trackArtist").description("트랙 아티스트")
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
    @DisplayName("친구가 가장 많이 들은 곡")
    public void findMostListenedTrackByFriend() throws Exception {
        // given
        final String url = "/tracks/friends";
        List<TrackPlayCountResponse> trackPlayCountResponses = new ArrayList<>();
        for(int i = 0; i < 2; i++) {
            trackPlayCountResponses.add(buildTrackPlayCountResponse(buildTrack()));
        }

        doReturn(MessageResponse.of(REQUEST_SUCCESS , trackPlayCountResponses)).when(playCountService).findMostListenedTrackByFriend(any(String.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .cookie(new Cookie("accessToken", "accessToken"))
        );

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("track/mostListenTrackByFriend",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 access token")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("데이터"),
                                fieldWithPath("data.[].playCount").description("재생 횟수"),
                                fieldWithPath("data.[].trackId").description("스포티파이 트랙 ID"),
                                fieldWithPath("data.[].trackImageUrl").description("트랙 이미지 URL"),
                                fieldWithPath("data.[].trackName").description("트랙 이름"),
                                fieldWithPath("data.[].trackArtist").description("트랙 아티스트")
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
    @DisplayName("트랙 추천 수 증가 실패 _ 트랙 탐색 실패")
    public void countPlayCountFail_notFountTrack() throws Exception {
        // given
        final String url = "/tracks/play-count/{trackId}";
        doThrow(new TrackException("트랙을 찾을 수 없습니다.")).when(playCountService).modifyPlayCount(any(String.class) , any(String.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.patch(url , "trackId")
                        .cookie(new Cookie("accessToken", "accessToken"))
        );

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("track/addTrackListenedCountFail-notFountTrack",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("trackId").description("스포티파이 트랙 ID")
                        ),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 access token")
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

        assertThat(result.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE.getCode());
        assertThat(result.getMessage()).isEqualTo("트랙을 찾을 수 없습니다.");
    }

    public TrackPlayCountResponse buildTrackPlayCountResponse(Track track) {
        return TrackPlayCountResponse.builder()
                .playCount(0)
                .track(track)
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

}
