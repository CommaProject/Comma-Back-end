package com.team.comma.domain.spotify.playlist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doReturn;

import com.team.comma.domain.playlist.playlist.service.PlaylistService;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.playlistTrack.domain.PlaylistTrack;
import com.team.comma.domain.playlist.playlist.dto.PlaylistResponse;
import com.team.comma.domain.playlist.playlist.dto.PlaylistUpdateRequest;
import com.team.comma.domain.playlist.playlist.repository.PlaylistRepository;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.trackArtist.TrackArtist;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.security.auth.login.AccountException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

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
        final Optional<User> optionalUser = Optional.of(user);

        final TrackArtist trackArtist = buildTrackArtist();
        final Track track = buildTrack(Arrays.asList(trackArtist));
        final PlaylistTrack playlistTrack = buildPlaylistTrack(track);

        final PlaylistResponse playlistResponse = PlaylistResponse.of(buildUserPlaylist(Arrays.asList(playlistTrack)), 3, "representative album image url");
        final List<PlaylistResponse> playlistResponseList = List.of(playlistResponse);

        doReturn(optionalUser).when(userRepository).findByEmail(userEmail);
        doReturn(userEmail).when(jwtTokenProvider).getUserPk(token);
        doReturn(playlistResponseList).when(playlistRepository).getPlaylistsByUser(user);

        // when
        final MessageResponse result = playlistService.getPlaylists(token);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
        assertThat((List<PlaylistResponse>) result.getData()).isEqualTo(playlistResponseList);
    }

    @Test
    void 플레이리스트_알람설정변경_실패_플레이리스트_찾을수없음() {
        // given

        // when
        final Throwable thrown = catchThrowable(() -> playlistService.updatePlaylistAlarmFlag(123L, false));

        // then
        assertThat(thrown.getMessage()).isEqualTo("플레이리스트를 찾을 수 없습니다.");
    }

    @Test
    void 플레이리스트_알람설정변경_성공() {
        // given
        final TrackArtist trackArtist = buildTrackArtist();
        final Track track = buildTrack(Arrays.asList(trackArtist));
        final PlaylistTrack playlistTrack = buildPlaylistTrack(track);
        final Playlist userPlaylist = buildUserPlaylist(Arrays.asList(playlistTrack));
        final Optional<Playlist> optionalPlaylist = Optional.of(userPlaylist);
        doReturn(optionalPlaylist).when(playlistRepository).findById(userPlaylist.getId());

        // when
        final MessageResponse result = playlistService.updatePlaylistAlarmFlag(userPlaylist.getId(), false);

        // then
        assertThat(result.getCode()).isEqualTo(2);
        assertThat(result.getMessage()).isEqualTo("알람 설정이 변경되었습니다.");
    }

    @Test
    void 플레이리스트_삭제_실패_플레이리스트_찾을수없음() {
        // given
        final List<Long> playlistIdList = Arrays.asList(123L, 124L);

        // when
        final Throwable thrown = catchThrowable(() -> playlistService.updatePlaylistsDelFlag(playlistIdList));

        // then
        assertThat(thrown.getMessage()).isEqualTo("플레이리스트를 찾을 수 없습니다.");
    }

    @Test
    void 플레이리스트_삭제_성공() {
        // given
        final TrackArtist trackArtist = buildTrackArtist();
        final Track track = buildTrack(Arrays.asList(trackArtist));
        final PlaylistTrack playlistTrack = buildPlaylistTrack(track);
        final Playlist userPlaylist = buildUserPlaylist(Arrays.asList(playlistTrack));
        Optional<Playlist> optionalPlaylist = Optional.of(userPlaylist);
        doReturn(optionalPlaylist).when(playlistRepository).findById(userPlaylist.getId());

        final List<Long> playlistIdList = Arrays.asList(userPlaylist.getId());

        // when
        final MessageResponse result = playlistService.updatePlaylistsDelFlag(playlistIdList);

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

    private Playlist buildUserPlaylist(List<PlaylistTrack> playlistTrackList) {
        return Playlist.builder()
                .id(1L)
                .alarmFlag(true)
                .playlistTrackList(playlistTrackList)
                .build();
    }

    private PlaylistTrack buildPlaylistTrack(Track track) {
        return PlaylistTrack.builder()
                .track(track)
                .trackAlarmFlag(true)
                .build();
    }

    private Track buildTrack(List<TrackArtist> trackArtistList) {
        return Track.builder()
                .id(1L)
                .trackArtistList(trackArtistList)
                .build();
    }

    private TrackArtist buildTrackArtist(){
        return TrackArtist.builder()
                .id(1L)
                .artistName("test artist")
                .build();
    }

}
