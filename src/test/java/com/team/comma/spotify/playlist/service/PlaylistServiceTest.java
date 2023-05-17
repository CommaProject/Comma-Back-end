package com.team.comma.spotify.playlist.service;

import static com.team.comma.common.constant.ResponseCodeEnum.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.domain.PlaylistTrack;
import com.team.comma.spotify.playlist.dto.PlaylistRequest;
import com.team.comma.spotify.playlist.dto.PlaylistResponse;
import com.team.comma.spotify.playlist.dto.PlaylistUpdateRequest;
import com.team.comma.spotify.playlist.repository.PlaylistRepository;
import com.team.comma.spotify.track.domain.Track;
import com.team.comma.spotify.track.domain.TrackArtist;
import com.team.comma.user.constant.UserRole;
import com.team.comma.user.constant.UserType;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.security.auth.login.AccountException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
// 커밋 테스트
@ExtendWith(MockitoExtension.class)
class PlaylistServiceTest {

    @InjectMocks
    private PlaylistService playlistService;
    @Mock
    private PlaylistRepository playlistRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private String userEmail = "email@naver.com";
    private String token = "accessToken";

    @Test
    void 플레이리스트_조회_성공() throws AccountException {
        // given
        final User user = buildUserWithEmail();
        Optional<User> optionalUser = Optional.of(user);
        doReturn(optionalUser).when(userRepository).findByEmail(userEmail);
        doReturn(userEmail).when(jwtTokenProvider).getUserPk(token);

        final List<Playlist> userPlaylist = Arrays.asList(
                buildUserPlaylist(),
                buildUserPlaylist(),
                buildUserPlaylist()
        );
        doReturn(userPlaylist).when(playlistRepository).findAllByUser(user);

        // when
        final List<PlaylistResponse> result = playlistService.getPlaylists(token);

        // then
        assertThat(result).hasSize(3);
    }

    @Test
    void 플레이리스트_알람설정변경_실패_플레이리스트_찾을수없음() {
        // given

        // when
        final Throwable thrown = catchThrowable(() -> playlistService.updateAlarmFlag(123L, false));

        // then
        assertThat(thrown.getMessage()).isEqualTo("플레이리스트를 찾을 수 없습니다.");
    }

    @Test
    void 플레이리스트_알람설정변경_성공() {
        // given
        final Playlist userPlaylist = buildUserPlaylist();
        Optional<Playlist> optionalPlaylist = Optional.of(userPlaylist);
        doReturn(optionalPlaylist).when(playlistRepository).findById(userPlaylist.getId());

        // when
        final MessageResponse result = playlistService.updateAlarmFlag(userPlaylist.getId(), false);

        // then
        assertThat(result.getCode()).isEqualTo(2);
        assertThat(result.getMessage()).isEqualTo("알람 설정이 변경되었습니다.");
    }

    @Test
    void 플레이리스트_삭제_실패_플레이리스트_찾을수없음() {
        // given
        List<PlaylistRequest> playlistRequests = Arrays.asList(
                PlaylistRequest.builder().playlistId(123L).build(),
                PlaylistRequest.builder().playlistId(124L).build());

        // when
        final Throwable thrown = catchThrowable(() -> playlistService.deletePlaylist(playlistRequests));

        // then
        assertThat(thrown.getMessage()).isEqualTo("플레이리스트를 찾을 수 없습니다.");
    }

    @Test
    void 플레이리스트_삭제_성공() {
        // given
        final Playlist userPlaylist = buildUserPlaylist();
        Optional<Playlist> optionalPlaylist = Optional.of(userPlaylist);
        doReturn(optionalPlaylist).when(playlistRepository).findById(userPlaylist.getId());

        List<PlaylistRequest> playlistRequests = Arrays.asList(
                PlaylistRequest.builder().playlistId(userPlaylist.getId()).build());

        // when
        final MessageResponse result = playlistService.deletePlaylist(playlistRequests);

        // then
        assertThat(result.getCode()).isEqualTo(2);
        assertThat(result.getMessage()).isEqualTo("플레이리스트가 삭제되었습니다.");
    }

    @Test
    void 플레이리스트의_총재생시간을_리턴한다() {
        //given
        final long PLAYLIST_ID = 1L;
        final int TOTAL_DURATION_TIME = 100;

        doReturn(TOTAL_DURATION_TIME)
            .when(playlistRepository).getTotalDurationTimeMsWithPlaylistId(PLAYLIST_ID);

        //when
        MessageResponse<Integer> totalDurationTimeMsDto = playlistService.getTotalDurationTimeMsByPlaylist(
            PLAYLIST_ID);

        //then
        assertThat(totalDurationTimeMsDto.getData()).isEqualTo(TOTAL_DURATION_TIME);
    }

    @Test
    void 플리를_PlaylistRequest로_수정하고_MessageResponse를_반환한다() {
        //given
        PlaylistUpdateRequest playlistRequest = PlaylistUpdateRequest.builder()
            .id(1L)
            .playlistTitle("플리제목수정")
            .alarmStartTime(LocalTime.now())
            .listSequence(2)
            .build();

        doReturn(Optional.of(buildPlaylist()))
            .when(playlistRepository).findById(playlistRequest.getId());

        //when
        MessageResponse messageResponse = playlistService.updatePlaylist(playlistRequest);

        //then
        assertThat(messageResponse.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(messageResponse.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    private Playlist buildPlaylist() {
        return Playlist.builder()
            .user(buildUserWithEmail())
            .build();
    }

    private User buildUserWithEmail() {
        return User.builder().email(userEmail).build();
    }

    private Playlist buildUserPlaylist() {
        final List<TrackArtist> artistList = Arrays.asList(
                TrackArtist.builder().id(1L).build()
        );

        final Track track = Track.builder()
                .id(1L)
                .trackArtistList(artistList)
                .build();

        final List<PlaylistTrack> playlistTrack = Arrays.asList(
                PlaylistTrack.builder().track(track).trackAlarmFlag(true).build()
        );

        return Playlist.builder().id(1L).alarmFlag(true).playlistTrackList(playlistTrack).build();
    }

}
