package com.team.comma.service;

import com.team.comma.domain.Playlist;
import com.team.comma.domain.PlaylistTrack;
import com.team.comma.domain.Track;
import com.team.comma.domain.TrackArtist;
import com.team.comma.dto.PlaylistResponse;
import com.team.comma.dto.PlaylistTrackArtistResponse;
import com.team.comma.dto.PlaylistTrackResponse;
import com.team.comma.repository.PlaylistRepository;
import com.team.comma.repository.PlaylistTrackRepository;
import com.team.comma.repository.TrackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlaylistServiceTest {
    @InjectMocks
    private PlaylistService playlistService;
    @Mock
    private PlaylistTrackRepository playlistTrackRepository;
    @Mock
    private PlaylistRepository playlistRepository;
    @Mock
    private TrackRepository trackRepository;

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
