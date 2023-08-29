package com.team.comma.domain.playlist.playlist.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.track.domain.PlaylistTrack;
import com.team.comma.domain.playlist.playlist.dto.PlaylistResponse;
import com.team.comma.domain.playlist.playlist.dto.PlaylistUpdateRequest;
import com.team.comma.domain.playlist.track.repository.PlaylistTrackRepository;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.track.repository.TrackRepository;
import com.team.comma.domain.user.user.constant.UserRole;
import com.team.comma.domain.user.user.constant.UserType;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.config.TestConfig;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(TestConfig.class)
class PlaylistRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private PlaylistTrackRepository playlistTrackRepository;

    @Autowired
    private TrackRepository trackRepository;

    private final String userEmail = "email@naver.com";
    private final String title = "test playlist";

    @Test
    void 플레이리스트_저장() {
        // given
        final User user = userRepository.save(buildUser());

        // when
        final Playlist result = playlistRepository.save(buildPlaylist(user, title));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPlaylistTitle()).isEqualTo(title);
    }

    @Test
    void 플레이리스트_조회_0개() {
        // given
        final User user = userRepository.save(buildUser());

        // when
        final List<PlaylistResponse> result = playlistRepository.findAllPlaylistsByUser(user);

        // then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void 플레이리스트_조회_2개() {
        // given
        final User user = userRepository.save(buildUser());

        Playlist playlist1 = buildPlaylist(user, title);
        Playlist playlist2 = buildPlaylist(user, title);
        playlistRepository.save(playlist1);
        playlistRepository.save(playlist2);

        Track track1 = buildTrackWithDurationTimeMs(1000);
        Track track2 = buildTrackWithDurationTimeMs(2000);
        trackRepository.save(track1);
        trackRepository.save(track2);

        PlaylistTrack playlistTrack1 = buildPlaylistTrackWithPlaylistAndTrack(playlist1, track1);
        PlaylistTrack playlistTrack2 = buildPlaylistTrackWithPlaylistAndTrack(playlist2, track2);
        playlistTrackRepository.save(playlistTrack1);
        playlistTrackRepository.save(playlistTrack2);

        // when
        final List<PlaylistResponse> result = playlistRepository.findAllPlaylistsByUser(user);

        // then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void 플레이리스트_아이디조회() {
        // given
        final User user = userRepository.save(buildUser());

        Playlist playlist1 = buildPlaylist(user, title);
        playlistRepository.save(playlist1);

        Track track1 = buildTrackWithDurationTimeMs(1000);
        trackRepository.save(track1);

        PlaylistTrack playlistTrack1 = buildPlaylistTrackWithPlaylistAndTrack(playlist1, track1);
        playlistTrackRepository.save(playlistTrack1);

        // when
        final PlaylistResponse result = playlistRepository.findPlaylistsByPlaylistId(playlist1.getId()).get();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPlaylistTitle()).isEqualTo(playlist1.getPlaylistTitle());
    }

    @Test
    void 플레이리스트_알람설정변경() {
        // given
        final User user = userRepository.save(buildUser());
        final Playlist playlist = playlistRepository.save(buildPlaylist(user, "test playlist"));

        // when
        long result = playlistRepository.modifyAlarmFlag(playlist.getId(), false);

        // then
        assertThat(result).isEqualTo(1);
    }

    @Test
    void 플레이리스트_삭제(){
        // given
        final User user = userRepository.save(buildUser());
        final Playlist playlist = playlistRepository.save(buildPlaylist(user, "test playlist"));

        // when
        long result = playlistRepository.deletePlaylist(playlist.getId());

        // then
        assertThat(result).isEqualTo(1);
    }

    @Test
    void 특정_플리에_트랙이_없다면_플리의_총재생시간은_0으로_리턴() {
        //given
        Playlist playlist = buildPlaylist();
        playlistRepository.save(playlist);

        //when
        int durationTimeSum = playlistRepository.findTotalDurationTimeMsWithPlaylistId(
            playlist.getId());

        //then
        assertThat(durationTimeSum).isEqualTo(0);
    }

    @Test
    void 하나의_플리의_총재생시간을_리턴() {
        // given
        Playlist playlist = buildPlaylist();
        playlistRepository.save(playlist);

        Track track1 = buildTrackWithDurationTimeMs(1000);
        Track track2 = buildTrackWithDurationTimeMs(2000);
        trackRepository.save(track1);
        trackRepository.save(track2);

        PlaylistTrack playlistTrack1 = buildPlaylistTrackWithPlaylistAndTrack(playlist, track1);
        PlaylistTrack playlistTrack2 = buildPlaylistTrackWithPlaylistAndTrack(playlist, track2);
        playlistTrackRepository.save(playlistTrack1);
        playlistTrackRepository.save(playlistTrack2);

        playlist.addPlaylistTrack(track1);
        playlist.addPlaylistTrack(track2);
        playlistRepository.save(playlist);
        // when
        int durationSum = playlistRepository.findTotalDurationTimeMsWithPlaylistId(playlist.getId());

        // then
        assertThat(durationSum).isEqualTo(3000L);
    }


    @Test
    void 플리간_순서중_제일_큰값을_리턴한다() {
        //given
        Playlist playlist1 = buildPlaylistWithListSequence(1);
        playlistRepository.save(playlist1);

        Playlist playlist2 = buildPlaylistWithListSequence(2);
        playlistRepository.save(playlist2);

        //when
        int maxListSequence = playlistRepository.findMaxListSequence();
        //then
        assertThat(maxListSequence).isEqualTo(2);
    }

    @Test
    void 플리가_존재하지않으면_플리_listSequence_0_리턴() {
        //given
        //when
        int maxListSequence = playlistRepository.findMaxListSequence();

        //then
        assertThat(maxListSequence).isZero();
    }

    @Test
    void 플리를_PlaylistRequest_로_저장한다() {
        //given
        User user = User.builder().email("test@email.com").build();
        userRepository.save(user);

        PlaylistUpdateRequest playlistUpdateRequest = PlaylistUpdateRequest.builder()
            .playlistTitle("플리제목")
            .alarmStartTime(LocalTime.now())
            .user(user)
            .listSequence(1)
            .build();

        Playlist playlist = playlistUpdateRequest.toEntity();
        //when
        Playlist savedPlaylist = playlistRepository.save(playlist);

        //then
        assertThat(savedPlaylist.getPlaylistTitle()).isEqualTo(playlist.getPlaylistTitle());
        assertThat(savedPlaylist.getAlarmStartTime()).isEqualTo(playlist.getAlarmStartTime());
        assertThat(savedPlaylist.getUser()).isEqualTo(playlist.getUser());
        assertThat(savedPlaylist.getListSequence()).isEqualTo(playlist.getListSequence());

    }

    @Test
    void 플리의_내용을_수정한다() {
        //given
        Playlist playlist = buildPlaylist();
        playlistRepository.save(playlist);

        PlaylistUpdateRequest playlistUpdateRequest = PlaylistUpdateRequest.builder()
            .id(playlist.getId())
            .playlistTitle("플리제목변경")
            .alarmStartTime(LocalTime.now())
            .listSequence(2)
            .build();

        //when
        playlist.modifyPlaylist(playlistUpdateRequest);

        Playlist updatedPlaylist = playlistRepository.findById(playlistUpdateRequest.getId()).get();

        //then
        assertThat(updatedPlaylist.getPlaylistTitle()).isEqualTo(
            playlistUpdateRequest.getPlaylistTitle());
        assertThat(updatedPlaylist.getListSequence()).isEqualTo(
            playlistUpdateRequest.getListSequence());
    }

    private User buildUser() {
        return User.builder()
                .email(userEmail)
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

    private Playlist buildPlaylist() {
        return Playlist.builder()
            .playlistTitle("My Playlist")
            .alarmFlag(false)
            .build();
    }

    private Playlist buildPlaylistWithListSequence(int listSequence) {
        return Playlist.builder()
                .listSequence(listSequence)
                .build();
    }

    private Track buildTrackWithDurationTimeMs(int durationTimeMs) {
        return Track.builder()
                .trackTitle(title)
                .albumImageUrl("url")
                .spotifyTrackId("trackId")
                .spotifyTrackHref("href")
                .durationTimeMs(durationTimeMs)
                .build();
    }

    private PlaylistTrack buildPlaylistTrackWithPlaylistAndTrack(Playlist playlist, Track track1) {
        return PlaylistTrack.builder()
                .playlist(playlist)
                .track(track1)
                .build();
    }

}
