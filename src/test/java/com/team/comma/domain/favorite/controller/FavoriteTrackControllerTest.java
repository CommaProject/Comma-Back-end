package com.team.comma.domain.favorite.controller;

import com.google.gson.Gson;
import com.team.comma.domain.favorite.service.FavoriteTrackService;
import com.team.comma.domain.track.controller.TrackController;
import com.team.comma.domain.track.dto.TrackRequest;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.security.auth.login.AccountException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(FavoriteTrackController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebAppConfiguration
public class FavoriteTrackControllerTest {

    @MockBean
    FavoriteTrackService favoriteTrackService;

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
    @DisplayName("트랙 좋아요 추가 실패 _ 사용자 정보를 찾을 수 없음")
    public void createFavoriteTrackFail_NotFoundUser() throws Exception {
        // given
        final String url = "/favorite/track";
        TrackRequest trackRequest = TrackRequest.builder()
                .trackTitle("title")
                .albumImageUrl("imageUrl")
                .spotifyTrackId("trackId")
                .spotifyTrackHref("trackHref")
                .durationTimeMs(0)
                .trackArtistList(Arrays.asList("artist1", "artist2"))
                .build();
        doThrow(new AccountException("사용자 정보를 찾을 수 없습니다.")).when(favoriteTrackService).createFavoriteTrack(any(String.class) , any(TrackRequest.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.post(url)
                        .content(gson.toJson(trackRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken", "accessToken"))
        );

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("track/add-favorite-track-fail-notFoundUser",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 access token")
                        ),
                        requestFields(
                                fieldWithPath("trackTitle").description("트랙 제목"),
                                fieldWithPath("albumImageUrl").description("트랙 이미지 URL"),
                                fieldWithPath("spotifyTrackId").description("스포티파이 트랙 ID"),
                                fieldWithPath("spotifyTrackHref").description("스포티 파이 재생 주소"),
                                fieldWithPath("durationTimeMs").description("재생 시간"),
                                fieldWithPath("trackArtistList[]").description("아티스트 명")
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
        assertThat(result.getMessage()).isEqualTo("사용자 정보를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("트랙 좋아요 추가")
    public void createFavoriteTrack() throws Exception {
        // given
        final String url = "/favorite/track";
        TrackRequest trackRequest = TrackRequest.builder()
                .trackTitle("title")
                .albumImageUrl("imageUrl")
                .spotifyTrackId("trackId")
                .spotifyTrackHref("trackHref")
                .durationTimeMs(0)
                .trackArtistList(Arrays.asList("artist1", "artist2"))
                .build();
        doReturn(MessageResponse.of(REQUEST_SUCCESS)).when(favoriteTrackService).createFavoriteTrack(any(String.class) , any(TrackRequest.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.post(url)
                        .content(gson.toJson(trackRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken", "accessToken"))
        );

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("track/add-favorite-track",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 access token")
                        ),
                        requestFields(
                                fieldWithPath("trackTitle").description("트랙 제목"),
                                fieldWithPath("albumImageUrl").description("트랙 이미지 URL"),
                                fieldWithPath("spotifyTrackId").description("스포티파이 트랙 ID"),
                                fieldWithPath("spotifyTrackHref").description("스포티 파이 재생 주소"),
                                fieldWithPath("durationTimeMs").description("재생 시간"),
                                fieldWithPath("trackArtistList[]").description("아티스트 명")
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
}
