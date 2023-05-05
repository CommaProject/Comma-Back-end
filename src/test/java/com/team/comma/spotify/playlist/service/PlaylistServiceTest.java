package com.team.comma.spotify.playlist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.Exception.PlaylistException;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.domain.PlaylistTrack;
import com.team.comma.spotify.playlist.dto.PlaylistResponse;
import com.team.comma.spotify.playlist.repository.PlaylistRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.team.comma.spotify.track.domain.Track;
import com.team.comma.spotify.track.domain.TrackArtist;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PlaylistServiceTest {
    @InjectMocks
    private PlaylistService playlistService;
    @Mock
    private PlaylistRepository playlistRepository;
    @Mock
    private UserRepository userRepository;

    private String userEmail = "email@naver.com";

    private Long playlistId = 123L;

    private Boolean flag = false;

    @Test
    public void 플레이리스트_조회() {
        // given
        final User user = User.builder().email(userEmail).build();
        doReturn(user).when(userRepository).findByEmail(user.getEmail());

        final List<TrackArtist> artistList = Arrays.asList(
                TrackArtist.builder().build()
        );

        final Track track = Track.builder()
                .trackArtistList(artistList)
                .build();

        final List<PlaylistTrack> playlistTrack = Arrays.asList(
                PlaylistTrack.builder().track(track).build()
        );

        doReturn(Arrays.asList(
                Playlist.builder().playlistTrackList(playlistTrack).build(),
                Playlist.builder().playlistTrackList(playlistTrack).build(),
                Playlist.builder().playlistTrackList(playlistTrack).build()
        )).when(playlistRepository).findAllByUser(user);

        // when
        final List<PlaylistResponse> result = playlistService.getPlaylist(userEmail);

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void 플레이리스트_알림설정변경_실패_존재하지않는플레이리스트() {
        // given

        // when
        final PlaylistException result = assertThrows(PlaylistException.class, () -> playlistService.updateAlarmFlag(playlistId, flag));

        // then
        assertThat(result.getMessage()).isEqualTo("알람 설정 변경에 실패했습니다. 플레이리스트를 찾을 수 없습니다.");
    }

    @Test
    public void 플레이리스트_알림설정변경_성공() {
        // given
        doReturn(Optional.of(Playlist.builder()
                .id(playlistId)
                .alarmFlag(false)
                .build()
        )).when(playlistRepository).findById(playlistId);

        // when
        final MessageResponse result = playlistService.updateAlarmFlag(playlistId,flag);

        // then
        assertThat(result.getCode()).isEqualTo(2);
        assertThat(result.getMessage()).isEqualTo("알람 설정이 변경되었습니다.");
    }
}
