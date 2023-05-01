package com.team.comma.spotify.playlist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.domain.PlaylistTrack;
import com.team.comma.spotify.playlist.dto.PlaylistResponse;
import com.team.comma.spotify.playlist.repository.PlaylistRepository;
import java.util.Arrays;
import java.util.List;

import com.team.comma.spotify.track.domain.Track;
import com.team.comma.spotify.track.domain.TrackArtist;
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

    private String userEmail = "email@naver.com";

    @Test
    public void 플레이리스트_조회() {
        // given
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
        )).when(playlistRepository).findAllByUser_Email(userEmail);

        // when
        final List<PlaylistResponse> result = playlistService.getPlaylist(userEmail);

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void 플레이리스트_알림설정변경_실패() {
        // given

        // when
        final int result = playlistService.updateAlarmFlag(123L, false);

        // then
        assertThat(result).isEqualTo(0);
    }

    @Test
    public void 플레이리스트_알림설정변경_성공() {
        // given
        when(playlistRepository.updateAlarmFlag(123L, false)).thenReturn(1);

        // when
        final int result = playlistService.updateAlarmFlag(123L, false);

        // then
        assertThat(result).isEqualTo(1);
    }
}
