package com.team.comma.domain.playlist.track.service;

import static com.team.comma.global.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import com.team.comma.domain.playlist.playlist.service.PlaylistService;
import com.team.comma.domain.track.track.service.TrackService;
import com.team.comma.global.message.MessageResponse;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.track.domain.PlaylistTrack;
import com.team.comma.domain.playlist.playlist.exception.PlaylistException;
import com.team.comma.domain.playlist.track.repository.PlaylistTrackRepository;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.user.user.domain.User;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaylistTrackServiceTest {

    @InjectMocks
    PlaylistTrackService playlistTrackService;
    @Mock
    PlaylistTrackRepository playlistTrackRepository;
    @Mock
    TrackService trackService;
    @Mock
    PlaylistService playlistService;

    private String spotifyTrackId = "input ISRC of track";

    @Test
    void createPlaylistTrack_Success() {
        // given
        User user = User.createUser();
        Track track = Track.buildTrack();
        Playlist playlist = Playlist.createPlaylist(user);

        doReturn(track).when(trackService).findTrackOrSave(anyString());
        doReturn(playlist).when(playlistService).findPlaylistOrThrow(anyLong());

        // when
        MessageResponse result = playlistTrackService.createPlaylistTrack(1L, "spotify track id");

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());

    }

    @Test
    void findPlaylistTrack_Success() throws PlaylistException {
        // given
        final long playlistId = 1L;
        final User user = User.createUser();
        final Playlist playlist = Playlist.createPlaylist(user);

        doReturn(playlist).when(playlistService).findPlaylistOrThrow(playlistId);

        // when
        final MessageResponse result = playlistTrackService.findPlaylistTrack(playlistId);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    @Test
    void findPlaylistTrack_Fail_PlaylistNotFound() {
        // given
        final long playlistId = 1L;
        doThrow(new PlaylistException("플레이리스트를 찾을 수 없습니다."))
                .when(playlistService).findPlaylistOrThrow(playlistId);

        // when
        final Throwable thrown = catchThrowable(() ->  playlistTrackService.findPlaylistTrack(playlistId));

        // then
        assertThat(thrown.getMessage()).isEqualTo("플레이리스트를 찾을 수 없습니다.");

    }

    @Test
    void modifyPlaylistTrackSequence_Success() {
        // given
        User user = User.createUser();
        Playlist playlist = Playlist.createPlaylist(user);
        Track track = Track.buildTrack();
        PlaylistTrack playlistTrack1 = PlaylistTrack.buildPlaylistTrack(playlist,track);
        PlaylistTrack playlistTrack2 = PlaylistTrack.buildPlaylistTrack(playlist,track);
        PlaylistTrack playlistTrack3 = PlaylistTrack.buildPlaylistTrack(playlist,track);

        doReturn(Optional.of(playlistTrack1)).when(playlistTrackRepository).findById(1L);
        doReturn(Optional.of(playlistTrack2)).when(playlistTrackRepository).findById(2L);
        doReturn(Optional.of(playlistTrack3)).when(playlistTrackRepository).findById(3L);

        // when
        MessageResponse result = playlistTrackService.modifyPlaylistTrackSequence(List.of(3L, 2L, 1L));

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());

        assertThat(playlistTrack3.getPlaySequence()).isEqualTo(1);
        assertThat(playlistTrack2.getPlaySequence()).isEqualTo(2);
        assertThat(playlistTrack1.getPlaySequence()).isEqualTo(3);

    }

    @Test
    void modifyPlaylistTrackAlarmFlag_Success() {
        // given
        User user = User.createUser();
        Track track = Track.buildTrack();
        Playlist playlist = Playlist.createPlaylist(user);
        PlaylistTrack playlistTrack = PlaylistTrack.buildPlaylistTrack(playlist,track);
        doReturn(Optional.of(playlistTrack)).when(playlistTrackRepository).findById(anyLong());

        // when
        MessageResponse result = playlistTrackService.modifyPlaylistTrackAlarmFlag(1L);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
        assertThat(result.getData()).isEqualTo(true);
    }

    @Test
    void deletePlaylistTrack_Success() {
        //given
        final List<Long> playlistTrackIdList = List.of(1L, 2L, 3L);

        User user = User.createUser();
        Playlist playlist = Playlist.createPlaylist(user);
        Track track = Track.buildTrack();
        PlaylistTrack playlistTrack1 = PlaylistTrack.buildPlaylistTrack(playlist,track);
        PlaylistTrack playlistTrack2 = PlaylistTrack.buildPlaylistTrack(playlist,track);
        PlaylistTrack playlistTrack3 = PlaylistTrack.buildPlaylistTrack(playlist,track);
        playlistTrackRepository.saveAll(List.of(playlistTrack1,playlistTrack2,playlistTrack3));

        //when
        MessageResponse result = playlistTrackService.deletePlaylistTracks(playlistTrackIdList);

        //then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
        Optional<PlaylistTrack> pt1 = playlistTrackRepository.findById(playlistTrack1.getId());
        Optional<PlaylistTrack> pt2 = playlistTrackRepository.findById(playlistTrack2.getId());
        Optional<PlaylistTrack> pt3 = playlistTrackRepository.findById(playlistTrack3.getId());
        assertThat(pt1).isEmpty();
        assertThat(pt2).isEmpty();
        assertThat(pt3).isEmpty();
    }

}