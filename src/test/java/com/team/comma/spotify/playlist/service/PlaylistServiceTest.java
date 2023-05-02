package com.team.comma.spotify.playlist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.exception.PlaylistErrorResult;
import com.team.comma.spotify.playlist.exception.PlaylistException;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.domain.PlaylistTrack;
import com.team.comma.spotify.playlist.dto.PlaylistResponse;
import com.team.comma.spotify.playlist.repository.PlaylistRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.team.comma.spotify.track.domain.Track;
import com.team.comma.spotify.track.domain.TrackArtist;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class PlaylistServiceTest {
    @InjectMocks
    private PlaylistService playlistService;
    @Mock
    private PlaylistRepository playlistRepository;

    private String userEmail = "email@naver.com";

    private Long playlistId = 123L;

    private Boolean flag = false;

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
    public void 플레이리스트_알림설정변경_실패_존재하지않음() {
        // given
        doReturn(Optional.empty()).when(playlistRepository).findById(123L);

        // when
        final PlaylistException result = assertThrows(PlaylistException.class, () -> playlistService.updateAlarmFlag(playlistId, flag));

        // then
        assertThat(result.getErrorResult()).isEqualTo(PlaylistErrorResult.PLAYLIST_NOT_FOUND);
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
        MessageResponse result = playlistService.updateAlarmFlag(playlistId,flag);

        // then
        assertThat(result.getCode()).isEqualTo(2);
        assertThat(result.getMessage()).isEqualTo("알람 설정이 변경되었습니다.");
    }
}
