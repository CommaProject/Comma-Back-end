package com.team.comma.service;

import com.team.comma.constant.UserRole;
import com.team.comma.constant.UserType;
import com.team.comma.domain.Playlist;
import com.team.comma.domain.PlaylistTrack;
import com.team.comma.domain.User;
import com.team.comma.repository.PlaylistRepository;
import com.team.comma.repository.PlaylistTrackRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlaylistServiceTest {
    @InjectMocks
    private PlaylistService playlistService;
    @Mock
    private PlaylistTrackRepository playlistTrackRepository;
    @Mock
    private PlaylistRepository playlistRepository;

    private String userEmail = "email@naver.com";

    @Test
    public void 플레이리스트_조회_성공() {
        // given
        doReturn(Arrays.asList(
                Playlist.builder().build(),
                Playlist.builder().build(),
                Playlist.builder().build()
        )).when(playlistRepository).findAllByUser_Email(userEmail);

        // when
        final List<Playlist> result = playlistService.getPlaylist(userEmail);

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void 플레이리스트_곡조회_성공() {
        // given
        doReturn(Arrays.asList(
                PlaylistTrack.builder().build(),
                PlaylistTrack.builder().build(),
                PlaylistTrack.builder().build()
        )).when(playlistTrackRepository).findAllByPlaylist_Id(123L);

        // when
        final List<PlaylistTrack> result = playlistService.getPlaylistTrack(123L);

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void 사용자플레이리스트_조회_성공() {
        // given
        doReturn(Arrays.asList(
                PlaylistTrack.builder().build(),
                PlaylistTrack.builder().build(),
                PlaylistTrack.builder().build()
        )).when(playlistTrackRepository).findAllByPlaylist_Id(123L);

        // when
        final List<PlaylistTrack> result = playlistService.getPlaylistTrack(123L);

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void 플레이리스트_알림설정변경_성공() {
        // given
        doReturn(Optional.of(Playlist.builder()
                .id(123L)
                .alarmFlag(false)
                .build()
        )).when(playlistRepository).findById(123L);

        // when
        final int result = playlistService.updateAlarmFlag(123L,false);
        final Optional<Playlist> resultPlaylist = playlistRepository.findById(123L);

        // then
        assertThat(result).isEqualTo(1);
        assertThat(resultPlaylist).isPresent();
        assertFalse(resultPlaylist.get().getAlarmFlag());
    }
}
