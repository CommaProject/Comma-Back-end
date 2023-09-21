package com.team.comma.domain.playlist.recommend.service;

import static com.team.comma.global.common.constant.ResponseCodeEnum.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.playlist.repository.PlaylistRepository;
import com.team.comma.domain.playlist.recommend.constant.RecommendListType;
import com.team.comma.domain.playlist.recommend.constant.RecommendType;
import com.team.comma.domain.playlist.recommend.domain.Recommend;
import com.team.comma.domain.playlist.recommend.dto.RecommendListRequest;
import com.team.comma.domain.playlist.recommend.dto.RecommendRequest;
import com.team.comma.domain.playlist.recommend.repository.RecommendRepository;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.detail.domain.UserDetail;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.security.auth.login.AccountException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class RecommendServiceTest {

    @InjectMocks
    private RecommendService recommendService;
    @Mock
    private RecommendRepository recommendRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PlaylistRepository playlistRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private String token = "accessToken";
    @Test
    void 추천_저장_실패_추천_보낸_사용자_정보_찾을수없음() {
        // given
        final RecommendRequest recommendRequest = buildRequest();

        // when
        final Throwable thrown = catchThrowable(() -> recommendService.addRecommend(token, recommendRequest));

        // then
        assertThat(thrown.getMessage()).isEqualTo(NOT_FOUNT_USER.getMessage());

    }

    @Test
    void 추천_저장_실패_추천_받는_사용자_정보_찾을수없음() {
        // given
        final User fromUser = buildUserWithEmailAndDetail("fromUser");
        doReturn(Optional.of(fromUser)).when(userRepository).findUserByEmail(fromUser.getEmail());
        doReturn(fromUser.getEmail()).when(jwtTokenProvider).getUserPk(token);

        final RecommendRequest recommendRequest = buildRequest();

        final Playlist playlist = buildPlaylistWithId(recommendRequest.getRecommendPlaylistId());
        doReturn(Optional.of(playlist)).when(playlistRepository).findById(playlist.getId());

        // when
        final Throwable thrown = catchThrowable(() -> recommendService.addRecommend(token, recommendRequest));

        // then
        assertThat(thrown.getMessage()).isEqualTo("추천 받는 사용자 정보가 올바르지 않습니다.");

    }

    @Test
    void 추천_저장_실패_플레이리스트_찾을수없음() {
        // given
        final User fromUser = buildUserWithEmailAndDetail("fromUser");
        doReturn(Optional.of(fromUser)).when(userRepository).findUserByEmail(fromUser.getEmail());
        doReturn(fromUser.getEmail()).when(jwtTokenProvider).getUserPk(token);

        final RecommendRequest recommendRequest = buildRequest();

        // when
        final Throwable thrown = catchThrowable(() -> recommendService.addRecommend(token, recommendRequest));

        // then
        assertThat(thrown.getMessage()).isEqualTo(PLAYLIST_NOT_FOUND.getMessage());

    }

    @Test
    void 추천_저장_실패_사용자에게_이미_추천한_플레이리스트() {
        // given
        final User fromUser = buildUserWithEmailAndDetail("fromUser");
        doReturn(Optional.of(fromUser)).when(userRepository).findUserByEmail(fromUser.getEmail());
        doReturn(fromUser.getEmail()).when(jwtTokenProvider).getUserPk(token);

        final RecommendRequest recommendRequest = buildRequest();

        final User toUser = buildUserWithEmailAndDetail(recommendRequest.getRecommendToEmail());
        doReturn(Optional.of(toUser)).when(userRepository).findUserByEmail(toUser.getEmail());

        final Playlist playlist = buildPlaylistWithId(recommendRequest.getRecommendPlaylistId());
        doReturn(Optional.of(playlist)).when(playlistRepository).findById(playlist.getId());

        doReturn(1L).when(recommendRepository).getRecommendCountByToUserAndPlaylist(any());

        // when
        final Throwable thrown = catchThrowable(() -> recommendService.addRecommend(token, recommendRequest));

        // then
        assertThat(thrown.getMessage()).isEqualTo("사용자에게 이미 추천한 플레이리스트 입니다.");

    }

    @Test
    void 추천_저장_성공() throws Exception {
        // given
        final User fromUser = buildUserWithEmailAndDetail("fromUser");
        doReturn(Optional.of(fromUser)).when(userRepository).findUserByEmail(fromUser.getEmail());
        doReturn(fromUser.getEmail()).when(jwtTokenProvider).getUserPk(token);

        final RecommendRequest recommendRequest = buildRequest();

        final User toUser = buildUserWithEmailAndDetail(recommendRequest.getRecommendToEmail());
        doReturn(Optional.of(toUser)).when(userRepository).findUserByEmail(toUser.getEmail());

        final Playlist playlist = buildPlaylistWithId(recommendRequest.getRecommendPlaylistId());
        doReturn(Optional.of(playlist)).when(playlistRepository).findById(playlist.getId());

        // when
        final MessageResponse result = recommendService.addRecommend(token, recommendRequest);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());

    }

    @Test
    void 추천_받은_리스트_조회_실패_사용자정보_찾을수없음() {
        // given
        final RecommendListRequest recommendListRequest = RecommendListRequest.builder().recommendListType(RecommendListType.RECIEVED).build();

        // when
        final Throwable thrown = catchThrowable(() -> recommendService.getRecommendList(token, recommendListRequest));

        // then
        assertThat(thrown.getMessage()).isEqualTo(NOT_FOUNT_USER.getMessage());
    }

    @Test
    void 추천_받은_리스트_조회_성공() throws AccountException {
        // given
        final User user = buildUserWithEmailAndDetail("toUser");
        doReturn(Optional.of(user)).when(userRepository).findUserByEmail(user.getEmail());
        doReturn(user.getEmail()).when(jwtTokenProvider).getUserPk(token);

        final RecommendListRequest recommendListRequest = RecommendListRequest.builder().recommendListType(RecommendListType.RECIEVED).build();

        // when
        final MessageResponse result = recommendService.getRecommendList(token, recommendListRequest);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    @Test
    void 추천_보낸_리스트_조회_성공() throws AccountException {
        // given
        final User user = buildUserWithEmailAndDetail("toUser");
        doReturn(Optional.of(user)).when(userRepository).findUserByEmail(user.getEmail());
        doReturn(user.getEmail()).when(jwtTokenProvider).getUserPk(token);

        final RecommendListRequest recommendListRequest = RecommendListRequest.builder().recommendListType(RecommendListType.RECIEVED).build();

        // when
        final MessageResponse result = recommendService.getRecommendList(token, recommendListRequest);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    @Test
    void 익명_추천_리스트_조회_성공() throws AccountException {
        // given
        final User user = buildUserWithEmailAndDetail("toUser");
        doReturn(Optional.of(user)).when(userRepository).findUserByEmail(user.getEmail());
        doReturn(user.getEmail()).when(jwtTokenProvider).getUserPk(token);

        final RecommendListRequest recommendListRequest = RecommendListRequest.builder().recommendListType(RecommendListType.ANONYMOUS).build();

        // when
        final MessageResponse result = recommendService.getRecommendList(token, recommendListRequest);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    @Test
    void 추천_정보_조회_실패_사용자정보_찾을수없음() {
        // given

        // when
        final Throwable thrown = catchThrowable(() -> recommendService.getRecommend(1L));

        // then
        assertThat(thrown.getMessage()).isEqualTo("추천 정보를 찾을 수 없습니다.");
    }

    @Test
    void 추천_정보_조회_성공() {
        // given
        final User fromUser = buildUserWithEmailAndDetail("fromUser");
        final RecommendRequest recommendRequest = buildRequest();
        final User toUser = buildUserWithEmailAndDetail(recommendRequest.getRecommendToEmail());

        final Playlist playlist = buildPlaylistWithId(recommendRequest.getRecommendPlaylistId());
        final Track track = Track.builder().id(1L).build();
        playlist.addPlaylistTrack(track);

        final Recommend recommend = buildRecommend(toUser, playlist);
        doReturn(Optional.of(recommend)).when(recommendRepository).findById(recommend.getId());

        // when
        final MessageResponse result = recommendService.getRecommend(recommend.getId());

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    private User buildUserWithEmailAndDetail(String email) {
        return User.builder()
                .email(email)
                .userDetail(UserDetail.builder().profileImageUrl("test").build())
                .build();
    }

    private Playlist buildPlaylistWithId(long id) {
        return Playlist.builder()
                .id(id)
                .alarmFlag(true)
                .build();
    }

    private Recommend buildRecommend(User toUser, Playlist playlist) {
        return Recommend.builder()
                .id(1L)
                .toUser(toUser)
                .recommendType(RecommendType.FOLLOWING)
                .comment("test recommend")
                .playlist(playlist)
                .playCount(0L)
                .build();
    }

    private RecommendRequest buildRequest() {
        return RecommendRequest.builder()
                .recommendPlaylistId(1L)
                .recommendType(RecommendType.FOLLOWING)
                .recommendToEmail("toUserEmail")
                .comment("test recommend")
                .build();
    }

}
