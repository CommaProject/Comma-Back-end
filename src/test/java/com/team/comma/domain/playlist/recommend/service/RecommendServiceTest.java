package com.team.comma.domain.playlist.recommend.service;

import static com.team.comma.global.constant.ResponseCodeEnum.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.team.comma.domain.playlist.playlist.service.PlaylistService;
import com.team.comma.domain.playlist.recommend.dto.RecommendResponse;
import com.team.comma.domain.user.detail.domain.UserDetail;
import com.team.comma.domain.user.user.exception.UserException;
import com.team.comma.domain.user.user.service.UserService;
import com.team.comma.global.message.MessageResponse;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.playlist.repository.PlaylistRepository;
import com.team.comma.domain.playlist.recommend.constant.RecommendListType;
import com.team.comma.domain.playlist.recommend.domain.Recommend;
import com.team.comma.domain.playlist.recommend.dto.RecommendRequest;
import com.team.comma.domain.playlist.recommend.repository.RecommendRepository;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.security.auth.login.AccountException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendServiceTest {

    @InjectMocks
    private RecommendService recommendService;
    @Mock
    private RecommendRepository recommendRepository;
    @Mock
    private UserService userService;
    @Mock
    private PlaylistService playlistService;
    @Mock
    private PlaylistRepository playlistRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void createPlaylistRecommend_Success() throws Exception {
        // given
        final User fromUser = User.createUser("fromUser");
        final User toUser = User.createUser("toUser");
        doReturn(fromUser).when(userService).findUserOrThrow(fromUser.getEmail());
        doReturn(toUser).when(userService).findUserOrThrow(toUser.getEmail());
        doReturn(fromUser.getEmail()).when(jwtTokenProvider).getUserPk("token");

        final Playlist playlist = Playlist.createPlaylist(1L, fromUser);
        doReturn(playlist).when(playlistService).findPlaylistOrThrow(playlist.getId());

        final Recommend recommend = Recommend.createRecommend("comment", playlist, toUser);
        final RecommendRequest recommendRequest = RecommendRequest.of(recommend);

        // when
        final MessageResponse result = recommendService.createPlaylistRecommend("token", recommendRequest);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());

    }

    @Test
    void createPlaylistRecommend_Fail_RecommendAlreadyExist() {
        // given
        final User fromUser = User.createUser("fromUser");
        final User toUser = User.createUser("toUser");
        doReturn(fromUser).when(userService).findUserOrThrow(fromUser.getEmail());
        doReturn(toUser).when(userService).findUserOrThrow(toUser.getEmail());
        doReturn(fromUser.getEmail()).when(jwtTokenProvider).getUserPk("token");

        final Playlist playlist = Playlist.createPlaylist(1L, fromUser);
        doReturn(playlist).when(playlistService).findPlaylistOrThrow(playlist.getId());
        doReturn(Optional.of(playlist)).when(recommendRepository).findByPlaylistAndToUser(playlist, toUser);

        final Recommend recommend = Recommend.createRecommend(1L, "comment", playlist, toUser);
        final RecommendRequest recommendRequest = RecommendRequest.of(recommend);

        // when
        final Throwable thrown = catchThrowable(() -> recommendService.createPlaylistRecommend("token", recommendRequest));

        // then
        assertThat(thrown.getMessage()).isEqualTo("사용자에게 이미 추천한 플레이리스트 입니다.");

    }
    @Test
    void findAllPlaylistRecommends_Success() throws AccountException {
        // given
        final User user = User.createUser("toUser");
        doReturn(user.getEmail()).when(jwtTokenProvider).getUserPk("token");

        // when
        final MessageResponse result = recommendService.findAllPlaylistRecommends("token", RecommendListType.RECIEVED);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    @Test
    void findRecommend_Success() {
        // given
        final User fromUser = User.createUser("fromUser");
        UserDetail.buildUserDetail(fromUser);
        final Track track = Track.buildTrack();
        final Playlist playlist = Playlist.createPlaylist(1L, fromUser);
        playlist.addPlaylistTrack(track);
        final Recommend recommend = Recommend.createRecommend(1L, "comment", playlist);

        doReturn(Optional.of(recommend)).when(recommendRepository).findById(recommend.getId());

        // when
        final MessageResponse result = recommendService.findRecommend("token", recommend.getId());

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

}