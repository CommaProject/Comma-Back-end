package com.team.comma.domain.playlist.playlist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

import com.team.comma.domain.alertday.service.AlertDayService;
import com.team.comma.domain.track.track.service.TrackService;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.track.domain.PlaylistTrack;
import com.team.comma.domain.playlist.playlist.dto.PlaylistResponse;
import com.team.comma.domain.playlist.playlist.dto.PlaylistModifyRequest;
import com.team.comma.domain.playlist.playlist.repository.PlaylistRepository;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.artist.domain.TrackArtist;
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
    private TrackService trackService;
    @Mock
    private PlaylistRepository playlistRepository;
    @Mock
    private AlertDayService alertDayService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private String userEmail = "email@naver.com";
    private String token = "accessToken";


    @Test
    public void createPlaylistSuccess() throws AccountException {
        // given
        String spotifyTrackId = "spotifyTrackId";
        Track track = Track.builder().build();
        doReturn(track).when(trackService).findTrackOrSave(spotifyTrackId);

        User user = User.buildUser("userEmail");
        doReturn(Optional.of(user)).when(userRepository).findUserByEmail(any());

        // when
        final MessageResponse result = playlistService.createPlaylist(token, spotifyTrackId);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());

    }

    @Test
    public void 플레이리스트_조회_성공() throws AccountException {
        // given
        final User user = User.buildUser("userEmail");
        final Optional<User> optionalUser = Optional.of(user);

        final TrackArtist trackArtist = buildTrackArtist();
        final Track track = buildTrack(Arrays.asList(trackArtist));
        final PlaylistTrack playlistTrack = buildPlaylistTrack(track);

        final PlaylistResponse playlistResponse = PlaylistResponse.of(
                buildUserPlaylist(Arrays.asList(playlistTrack)),
                "representative album image url",
                21000L);

        final List<PlaylistResponse> playlistResponseList = List.of(playlistResponse);

        doReturn(optionalUser).when(userRepository).findUserByEmail(userEmail);
        doReturn(userEmail).when(jwtTokenProvider).getUserPk(token);
        doReturn(playlistResponseList).when(playlistRepository).findAllPlaylistsByUser(user);

        // when
        final MessageResponse result = playlistService.findAllPlaylists(token);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
        assertThat((List<PlaylistResponse>) result.getData()).isEqualTo(playlistResponseList);
    }

    @Test
    public void 단일_플레이리스트_조회실패_존재하지않는플레이리스트() {
        // given
        doReturn(Optional.empty()).when(playlistRepository).findPlaylistByPlaylistId(30);

        // when
        Throwable throwable = catchThrowable(() -> playlistService.findPlaylist(30));

        // then
        assertThat(throwable.getMessage()).isEqualTo("플레이리스트를 찾을 수 없습니다.");
    }

    @Test
    public void 단일_플레이리스트_조회() {
        // given
        final TrackArtist trackArtist = buildTrackArtist();
        final Track track = buildTrack(Arrays.asList(trackArtist));
        final PlaylistTrack playlistTrack = buildPlaylistTrack(track);
        final PlaylistResponse playlistResponse = PlaylistResponse.of(buildUserPlaylist(
                Arrays.asList(playlistTrack)),
                "representative album image url",
                21000L);
        doReturn(Optional.ofNullable(playlistResponse)).when(playlistRepository).findPlaylistByPlaylistId(30);

        // when
        MessageResponse result = playlistService.findPlaylist(30);

        // then
        assertThat(result.getCode()).isEqualTo(1);
        assertThat(((PlaylistResponse) result.getData()).getPlaylistId()).isEqualTo(playlistResponse.getPlaylistId());
    }

    @Test
    public void 플레이리스트_알람설정변경_실패_플레이리스트_찾을수없음() {
        // given
        PlaylistModifyRequest request = PlaylistModifyRequest.builder().playlistId(1L).build();

        // when
        final Throwable thrown = catchThrowable(() -> playlistService.modifyPlaylistAlarmFlag(request));

        // then
        assertThat(thrown.getMessage()).isEqualTo("플레이리스트를 찾을 수 없습니다.");
    }

    @Test
    public void 플레이리스트_알람설정변경_성공() {
        // given
        final TrackArtist trackArtist = buildTrackArtist();
        final Track track = buildTrack(Arrays.asList(trackArtist));
        final PlaylistTrack playlistTrack = buildPlaylistTrack(track);
        final Playlist playlist = buildUserPlaylist(Arrays.asList(playlistTrack));
        final Optional<Playlist> optionalPlaylist = Optional.of(playlist);
        doReturn(optionalPlaylist).when(playlistRepository).findById(playlist.getId());

        PlaylistModifyRequest request = PlaylistModifyRequest.builder().playlistId(playlist.getId()).build();

        // when
        final MessageResponse result = playlistService.modifyPlaylistAlarmFlag(request);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    @Test
    public void 플레이리스트_삭제_실패_플레이리스트_찾을수없음() {
        // given
        final List<Long> playlistIdList = Arrays.asList(123L, 124L);

        // when
        final Throwable thrown = catchThrowable(() -> playlistService.modifyPlaylistsDelFlag(playlistIdList));

        // then
        assertThat(thrown.getMessage()).isEqualTo("플레이리스트를 찾을 수 없습니다.");
    }

    @Test
    public void 플레이리스트_삭제_성공() {
        // given
        final TrackArtist trackArtist = buildTrackArtist();
        final Track track = buildTrack(Arrays.asList(trackArtist));
        final PlaylistTrack playlistTrack = buildPlaylistTrack(track);
        final Playlist userPlaylist = buildUserPlaylist(Arrays.asList(playlistTrack));
        Optional<Playlist> optionalPlaylist = Optional.of(userPlaylist);
        doReturn(optionalPlaylist).when(playlistRepository).findById(userPlaylist.getId());

        final List<Long> playlistIdList = Arrays.asList(userPlaylist.getId());

        // when
        final MessageResponse result = playlistService.modifyPlaylistsDelFlag(playlistIdList);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    @Test
    public void 플레이리스트의_총재생시간을_리턴한다() {
        //given
        final long PLAYLIST_ID = 1L;
        final int TOTAL_DURATION_TIME = 100;

        doReturn(TOTAL_DURATION_TIME)
            .when(playlistRepository).findTotalDurationTimeMsByPlaylistId(PLAYLIST_ID);

        //when
        MessageResponse<Integer> totalDurationTimeMsDto = playlistService.findTotalDurationTimeMsByPlaylist(
            PLAYLIST_ID);

        //then
        assertThat(totalDurationTimeMsDto.getData()).isEqualTo(TOTAL_DURATION_TIME);
    }

    @Test
    public void modifyPlaylist_Success() {
        // given
        PlaylistModifyRequest request = PlaylistModifyRequest.builder()
                .playlistId(1L)
                .playlistTitle("플리제목수정")
                .alarmStartTime(LocalTime.now())
                .alarmDays(List.of(1,2,3))
                .build();

        User user = User.buildUser(userEmail);
        Playlist playlist = Playlist.buildPlaylist(user);
        doReturn(Optional.of(playlist)).when(playlistRepository).findById(anyLong());

        // when
        MessageResponse result = playlistService.modifyPlaylistAlarmDayAndTime(request);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
    }

    @Test
    public void 플레이리스트_제목_수정() {
        //given
        PlaylistModifyRequest playlistRequest = PlaylistModifyRequest.builder()
            .playlistId(1L)
            .playlistTitle("플리제목수정")
            .alarmStartTime(LocalTime.now())
            .build();

        User user = User.buildUser("userEmail");
        doReturn(Optional.of(Playlist.buildPlaylist(user)))
            .when(playlistRepository).findById(playlistRequest.getPlaylistId());

        //when
        MessageResponse messageResponse = playlistService.modifyPlaylistTitle(playlistRequest);

        //then
        assertThat(messageResponse.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(messageResponse.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
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
