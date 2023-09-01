package com.team.comma.domain.playlist.archive.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.team.comma.domain.playlist.archive.domain.Archive;
import com.team.comma.domain.playlist.archive.dto.ArchiveResponse;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.domain.playlist.archive.dto.ArchiveRequest;
import com.team.comma.domain.playlist.archive.service.ArchiveService;
import com.team.comma.domain.playlist.playlist.exception.PlaylistException;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.security.auth.login.AccountException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebAppConfiguration
@AutoConfigureRestDocs
@WebMvcTest(ArchiveController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class ArchiveControllerTest {
    @MockBean
    ArchiveService archiveService;

    MockMvc mockMvc;
    Gson gson;

    @BeforeEach
    public void init(RestDocumentationContextProvider restDocumentation,
                     WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();

        gson = new Gson();

    }

    @Test
    @DisplayName("아카이브 추가 실패 _ 사용자 정보를 찾을 수 없음")
    public void addArchiveFail_notFoundUser() throws Exception {
        // given
        final String api = "/archives";
        ArchiveRequest request = ArchiveRequest.builder().playlistId(0L).comment("comment").build();
        doThrow(new AccountException("사용자를 찾을 수 없습니다.")).when(archiveService).createArchive(any(String.class), any(ArchiveRequest.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken", "token")));

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("spotify/archive/addFail_notfoundUser",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        requestFields(
                                fieldWithPath("comment").description("아카이브 입력 데이터"),
                                fieldWithPath("playlistId").description("플레이리스트 Id")
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

        assertThat(result.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE.getCode());
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("아카이브 추가 실패 _ 플레이리스트 조회 실패")
    public void addArchiveFail_notFoundPlaylist() throws Exception {
        // given
        final String api = "/archives";
        ArchiveRequest request = ArchiveRequest.builder().playlistId(0L).comment("comment").build();
        doThrow(new PlaylistException("Playlist를 찾을 수 없습니다.")).when(archiveService).createArchive(any(String.class), any(ArchiveRequest.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken", "token")));

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("spotify/archive/addFail_notfoundPlaylist",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        requestFields(
                                fieldWithPath("comment").description("아카이브 입력 데이터"),
                                fieldWithPath("playlistId").description("플레이리스트 Id")
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
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("아카이브 추가 성공")
    public void addArchiveSuccess() throws Exception {
        // given
        final String api = "/archives";
        ArchiveRequest request = ArchiveRequest.builder().playlistId(0L).comment("comment").build();
        doReturn(MessageResponse.of(REQUEST_SUCCESS)).when(archiveService).createArchive(any(String.class), any(ArchiveRequest.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken", "token")));

        // then
        resultActions.andExpect(status().isCreated()).andDo(
                document("spotify/archive/addSuccess",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        requestFields(
                                fieldWithPath("comment").description("아카이브 입력 데이터"),
                                fieldWithPath("playlistId").description("플레이리스트 Id")
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
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("아카이브 Date로 조회 성공")
    public void findArchiveByDate() throws Exception {
        // given
        final String api = "/archives/{startDate}/{endDate}";
        User user = User.buildUser();
        Track track = buildTrack();
        Playlist playlist = buildPlaylist(user);
        playlist.addPlaylistTrack(track);
        Archive archive = Archive.buildArchive(user,"comment", playlist);
        ArchiveResponse archiveResponse = ArchiveResponse.of(archive);

        LocalDate startDate = LocalDate.of(2023,8,28);
        LocalDate endDate = LocalDate.now();

        doReturn(MessageResponse.of(REQUEST_SUCCESS, List.of(archiveResponse, archiveResponse))).when(archiveService).findArchiveByDate("token", startDate, endDate);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(api, startDate, endDate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken", "token")));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("playlist/archive/findSuccess",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터"),
                                fieldWithPath("data.[].archiveId").description("아카이브 Id"),
                                fieldWithPath("data.[].comment").description("코멘트 내용"),
                                fieldWithPath("data.[].createDate").description("작성 일자"),
                                fieldWithPath("data.[].publicFlag").description("공개 여부"),
                                fieldWithPath("data.[].playlistId").description("플레이리스트 Id"),
                                fieldWithPath("data.[].playlistTitle").description("플레이리스트 제목"),
                                fieldWithPath("data.[].albumImageUrl").description("대표 앨범 이미지 url")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat((List<ArchiveResponse>) result.getData()).size().isEqualTo(2);
    }

    public Track buildTrack(){
        return Track.builder()
                .id(1L)
                .trackTitle("title")
                .albumImageUrl("url")
                .spotifyTrackHref("href")
                .spotifyTrackId("id123")
                .build();
    }

    public Playlist buildPlaylist(User user){
        return Playlist.builder()
                .id(1L)
                .playlistTitle("새로운 플레이리스트")
                .user(user)
                .alarmFlag(true)
                .build();
    }

}
