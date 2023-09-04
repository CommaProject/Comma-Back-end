package com.team.comma.domain.playlist.track.service;

import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import com.team.comma.domain.playlist.playlist.service.PlaylistService;
import com.team.comma.domain.playlist.track.dto.PlaylistTrackModifyRequest;
import com.team.comma.domain.track.track.service.TrackService;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.track.domain.PlaylistTrack;
import com.team.comma.domain.playlist.track.dto.PlaylistTrackRequest;
import com.team.comma.domain.playlist.playlist.exception.PlaylistException;
import com.team.comma.domain.playlist.playlist.repository.PlaylistRepository;
import com.team.comma.domain.playlist.track.repository.PlaylistTrackRepository;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.user.user.constant.UserRole;
import com.team.comma.domain.user.user.constant.UserType;
import com.team.comma.domain.user.user.domain.User;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        User user = User.buildUser();
        Track track = buildTrack("track title");
        Playlist playlist = Playlist.buildPlaylist(user);

        doReturn(track).when(trackService).findTrackOrSave(anyString());
        doReturn(playlist).when(playlistService).findPlaylistOrThrow(anyLong());

        PlaylistTrackRequest playlistTrackRequest = PlaylistTrackRequest.builder()
                .playlistIdList(List.of(1L, 2L, 3L))
                .spotifyTrackId("spotify track id")
                .build();

        // when
        MessageResponse result = playlistTrackService.createPlaylistTrack(playlistTrackRequest);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());

    }

    @Test
    void modifyPlaylistTrackAlarmFlag_Success() {
        // given
        User user = User.buildUser();
        Track track = buildTrack("track title");
        Playlist playlist = Playlist.buildPlaylist(user);
        PlaylistTrack playlistTrack = PlaylistTrack.buildPlaylistTrack(playlist,track);
        doReturn(Optional.of(playlistTrack)).when(playlistTrackRepository).findByPlaylistIdAndTrackId(anyLong(), anyLong());

        PlaylistTrackModifyRequest request = PlaylistTrackModifyRequest.builder()
                .playlistId(1L).trackId(1L).build();

        // when
        MessageResponse result = playlistTrackService.modifyPlaylistTrackAlarmFlag(request);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
        assertThat(result.getData()).isEqualTo(true);
    }

    @Test
    void 플리에_담긴_트랙들을_삭제한다() {
        //given
        final Set<Long> trackIdList = Set.of(1L, 2L, 3L);
        final long playlistId = 1L;
        final int sizeOfTrackIdList = trackIdList.size();

        doReturn(1)
            .when(playlistTrackRepository)
            .deletePlaylistTrackByTrackIdAndPlaylistId(anyLong(), anyLong());

        doReturn(Optional.of(PlaylistTrack.builder().build()))
            .when(playlistTrackRepository)
                .findByPlaylistIdAndTrackId(anyLong(), anyLong());
        //when
        int deleteCount = (int) playlistTrackService.deletePlaylistTrack(trackIdList,
                playlistId)
            .getData();

        //then
        assertThat(deleteCount).isEqualTo(sizeOfTrackIdList);
    }

    @Test
    void 플리에_없는_트랙과의_관계를_끊을려고하면_에러_발생() {
        //given
        long playlistId = 1L;
        doThrow(EntityNotFoundException.class)
            .when(playlistTrackRepository)
            .findByPlaylistIdAndTrackId(anyLong(), anyLong());
        //when //then
        assertThrows(EntityNotFoundException.class,
            () -> {
                playlistTrackService.deletePlaylistTrack(Set.of(1L, 2L, 3L), playlistId);
            });

    }

    @Test
    void 플레이리스트_트랙_상세_리스트_조회_성공() throws PlaylistException {
        // given
        final long playlistId = 1L;
        final User user = buildUser();
        final Playlist playlist = buildPlaylist(user, "test playlist");

        doReturn(playlist).when(playlistService).findPlaylistOrThrow(playlistId);

        // when
        final MessageResponse result = playlistTrackService.findPlaylistTrack(playlistId);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    @Test
    void 플레이리스트_트랙_상세_리스트_조회_실패_플레이리스트없음() {
        // given
        final long playlistId = 1L;
        doThrow(new PlaylistException("플레이리스트를 찾을 수 없습니다."))
                .when(playlistService).findPlaylistOrThrow(playlistId);

        // when
        final Throwable thrown = catchThrowable(() ->  playlistTrackService.findPlaylistTrack(playlistId));

        // then
        assertThat(thrown.getMessage()).isEqualTo("플레이리스트를 찾을 수 없습니다.");

    }

    private User buildUser() {
        return User.builder()
                .email("email")
                .type(UserType.GENERAL_USER)
                .role(UserRole.USER)
                .build();
    }

    private Playlist buildPlaylist(User user, String title) {
        return Playlist.builder()
                .playlistTitle(title)
                .alarmFlag(true)
                .user(user)
                .build();
    }

    private Track buildTrack(String title) {
        return Track.builder()
                .trackTitle(title)
                .build();
    }

    private PlaylistTrack buildPlaylistTrack(Playlist playlist, Track track) {
        return PlaylistTrack.builder()
                .playlist(playlist)
                .track(track)
                .build();
    }

}