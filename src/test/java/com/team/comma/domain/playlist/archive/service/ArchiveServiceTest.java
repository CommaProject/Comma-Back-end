package com.team.comma.domain.playlist.archive.service;

import com.team.comma.domain.playlist.archive.domain.Archive;
import com.team.comma.domain.playlist.archive.dto.ArchiveResponse;
import com.team.comma.domain.playlist.track.repository.PlaylistTrackRepository;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.track.repository.TrackRepository;
import com.team.comma.domain.user.user.exception.UserException;
import com.team.comma.global.message.MessageResponse;
import com.team.comma.domain.playlist.archive.dto.ArchiveRequest;
import com.team.comma.domain.playlist.archive.repository.ArchiveRepository;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.playlist.exception.PlaylistException;
import com.team.comma.domain.playlist.playlist.repository.PlaylistRepository;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.security.auth.login.AccountException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.team.comma.global.constant.ResponseCodeEnum.NOT_FOUNT_USER;
import static com.team.comma.global.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class ArchiveServiceTest {

    @InjectMocks
    ArchiveService archiveService;

    @Mock
    ArchiveRepository archiveRepository;

    @Mock
    PlaylistRepository playlistRepository;

    @Mock
    TrackRepository trackRepository;

    @Mock
    PlaylistTrackRepository playlistTrackRepository;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("아카이브 추가 실패 _ 사용자 찾기 실패")
    public void addArchiveFail_notFoundUser() {
        // given
        doReturn("userEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.empty()).when(userRepository).findUserByEmail("userEmail");

        // when
        Throwable thrown = catchThrowable(() -> archiveService.createArchive("token" , null));

        // then
        assertThat(thrown).isInstanceOf(UserException.class).hasMessage(NOT_FOUNT_USER.getMessage());
    }

    @Test
    @DisplayName("아카이브 추가 실패 _ 플레이리스트 탐색 실패")
    public void addArchiveFail_notFoundPlaylist() {
        // given
        doReturn("userEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.of(User.builder().build())).when(userRepository).findUserByEmail("userEmail");
        doReturn(Optional.empty()).when(playlistRepository).findById(0L);
        ArchiveRequest archiveRequest = ArchiveRequest.builder().playlistId(0L).build();

        // when
        Throwable thrown = catchThrowable(() -> archiveService.createArchive("token" , archiveRequest));

        // then
        assertThat(thrown).isInstanceOf(PlaylistException.class).hasMessage("Playlist를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("아카이브 추가 성공")
    public void addArchiveSuccess() throws AccountException {
        // given
        doReturn("userEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.of(User.builder().build())).when(userRepository).findUserByEmail("userEmail");
        doReturn(Optional.of(Playlist.builder().build())).when(playlistRepository).findById(0L);
        ArchiveRequest archiveRequest = ArchiveRequest.builder().playlistId(0L).build();

        // when
        MessageResponse messageResponse = archiveService.createArchive("token" , archiveRequest);

        // then
        assertThat(messageResponse.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
    }

    @Test
    @DisplayName("아카이브 DateTime으로 조회")
    public void findArchiveByDateTime() {
        // given
        User user = User.createUser();
        Track track = buildTrack();
        Playlist playlist = buildPlaylist(user);
        playlist.addPlaylistTrack(track);

        Archive archive = Archive.buildArchive(user,"comment", playlist);
        ArchiveResponse archiveResponse = ArchiveResponse.of(archive);
        LocalDateTime startDateTime = LocalDate.of(2023,8,28).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.now().atTime(LocalTime.MAX);

        doReturn(List.of(archiveResponse)).when(archiveRepository).findArchiveByDateTime(user, startDateTime, endDateTime);

        // when
        List<ArchiveResponse> result = archiveService.findArchiveByDateTime(user, startDateTime, endDateTime);

        // then
        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    @DisplayName("아카이브 Date로 조회")
    public void findArchiveByDate() throws AccountException {
        // given
        User user = User.createUser();
        Track track = buildTrack();
        Playlist playlist = buildPlaylist(user);
        playlist.addPlaylistTrack(track);

        Archive archive = Archive.buildArchive(user,"comment", playlist);
        ArchiveResponse archiveResponse = ArchiveResponse.of(archive);

        LocalDate startDate = LocalDate.of(2023,8,28);
        LocalDate endDate = LocalDate.now();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        doReturn(user.getEmail()).when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.of(user)).when(userRepository).findUserByEmail(user.getEmail());
        doReturn(List.of(archiveResponse)).when(archiveRepository).findArchiveByDateTime(user, startDateTime, endDateTime);


        // when
        MessageResponse result = archiveService.findArchiveByDate("token", startDate, endDate);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());

    }

    public Track buildTrack(){
        return Track.builder()
                .id(1L)
                .trackTitle("title")
                .albumImageUrl("url")
                .spotifyTrackHref("href")
                .spotifyTrackId("id123")
                .build();
    }

    public Playlist buildPlaylist(User user){
        return Playlist.builder()
                .id(1L)
                .playlistTitle("새로운 플레이리스트")
                .user(user)
                .alarmFlag(true)
                .build();
    }

}
