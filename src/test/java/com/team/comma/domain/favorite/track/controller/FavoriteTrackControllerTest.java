package com.team.comma.domain.favorite.track.controller;

import com.google.gson.Gson;
import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.favorite.track.domain.FavoriteTrack;
import com.team.comma.domain.favorite.track.dto.FavoriteTrackRequest;
import com.team.comma.domain.favorite.track.dto.FavoriteTrackResponse;
import com.team.comma.domain.favorite.track.service.FavoriteTrackService;
import com.team.comma.domain.track.artist.domain.TrackArtist;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.track.dto.TrackArtistResponse;
import com.team.comma.domain.track.track.dto.TrackResponse;
import com.team.comma.domain.user.user.constant.UserRole;
import com.team.comma.domain.user.user.domain.User;
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
    public void createFavoriteTrackFail_UserNotFound() throws Exception {
        // given
        final String url = "/favorite/track";
        FavoriteTrackRequest favoriteTrackRequest = FavoriteTrackRequest.builder()
                .spotifyTrackId("trackId")
                .build();
        doThrow(new AccountException("사용자 정보를 찾을 수 없습니다.")).when(favoriteTrackService).createFavoriteTrack(any(String.class) , any(FavoriteTrackRequest.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.post(url)
                        .content(gson.toJson(favoriteTrackRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken", "accessToken"))
        );

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("favorite/create-favorite-track-fail",
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

        assertThat(result.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE.getCode());
        assertThat(result.getMessage()).isEqualTo("사용자 정보를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("트랙 좋아요 추가")
    public void createFavoriteTrack() throws Exception {
        // given
        final String url = "/favorite/track";
        FavoriteTrackRequest favoriteTrackRequest = FavoriteTrackRequest.builder()
                .spotifyTrackId("trackId")
                .build();
        doReturn(MessageResponse.of(REQUEST_SUCCESS)).when(favoriteTrackService).createFavoriteTrack(any(String.class) , any(FavoriteTrackRequest.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.post(url)
                        .content(gson.toJson(favoriteTrackRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken", "accessToken"))
        );

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("favorite/create-favorite-track",
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
    @DisplayName("트랙 좋아요 리스트 조회")
    public void findAllFavoriteTrack() throws Exception {
        // given
        final String url = "/favorite/track";

        User user = buildUser();
        Track track = buildTrack("track title", "spotifyId");
        FavoriteTrack favoriteTrack = buildFavoriteTrackWithTrackAndUser(track, user);
        Artist trackArtist = buildArtist("artist");

        TrackResponse trackResponse = buildTrackResponse("track title", "spotifyId");
        TrackArtistResponse trackArtistResponse = TrackArtistResponse.of(trackResponse , List.of(trackArtist));
        FavoriteTrackResponse favoriteTrackResponse = FavoriteTrackResponse.of(favoriteTrack, List.of(trackArtistResponse));

        doReturn(MessageResponse.of(REQUEST_SUCCESS, List.of(favoriteTrackResponse))).when(favoriteTrackService).findAllFavoriteTrack("accessToken");

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken", "accessToken"))
        );

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("favorite/find-all-favorite-track",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 access token")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("데이터"),
                                fieldWithPath("data.[].favoriteTrackId").description("최애 트랙 Id"),
                                fieldWithPath("data.[].trackArtistResponses[].track.id").description("트랙 Id"),
                                fieldWithPath("data.[].trackArtistResponses[].track.trackTitle").description("트랙 제목"),
                                fieldWithPath("data.[].trackArtistResponses[].track.durationTimeMs").description("트랙 재생 시간"),
                                fieldWithPath("data.[].trackArtistResponses[].track.recommendCount").description("트랙 추천 횟수"),
                                fieldWithPath("data.[].trackArtistResponses[].track.albumImageUrl").description("트랙 엘범 이미지 URL"),
                                fieldWithPath("data.[].trackArtistResponses[].track.spotifyTrackId").description("트랙 스포티파이 Id"),
                                fieldWithPath("data.[].trackArtistResponses[].track.spotifyTrackHref").description("트랙 스포티파이 주소"),
                                fieldWithPath("data.[].trackArtistResponses[].artists[].id").description("엔티티 식별자"),
                                fieldWithPath("data.[].trackArtistResponses[].artists[].spotifyArtistId").description("트랙 아티스트 Id"),
                                fieldWithPath("data.[].trackArtistResponses[].artists[].spotifyArtistName").description("트랙 아티스트 명")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    private FavoriteTrack buildFavoriteTrackWithTrackAndUser(Track track, User user){
        return FavoriteTrack.builder()
                .id(1L)
                .track(track)
                .user(user)
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

    private Track buildTrack(String title, String spotifyId) {
        return Track.builder()
                .id(1L)
                .trackTitle(title)
                .recommendCount(0L)
                .albumImageUrl("url")
                .spotifyTrackHref("spotifyTrackHref")
                .spotifyTrackId(spotifyId)
                .build();
    }

    public Artist buildArtist(String artist) {
        return Artist.builder()
                .spotifyArtistId("spotifyArtistId")
                .spotifyArtistName(artist)
                .build();
    }

    public TrackArtist buildTrackArtist(Track track , Artist artist) {
        return TrackArtist.builder()
                .id(1L)
                .track(track)
                .artist(artist)
                .build();
    }

    private User buildUser() {
        return User.builder()
                .id(1L)
                .email("email")
                .password("password")
                .role(UserRole.USER)
                .build();
    }

}
