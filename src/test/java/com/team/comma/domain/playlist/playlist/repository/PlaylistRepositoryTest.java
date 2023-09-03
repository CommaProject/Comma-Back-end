package com.team.comma.domain.playlist.playlist.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.track.domain.PlaylistTrack;
import com.team.comma.domain.playlist.playlist.dto.PlaylistResponse;
import com.team.comma.domain.playlist.track.repository.PlaylistTrackRepository;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.track.repository.TrackRepository;
import com.team.comma.domain.user.user.constant.UserRole;
import com.team.comma.domain.user.user.constant.UserType;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.config.TestConfig;
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
        final PlaylistResponse result = playlistRepository.findPlaylistByPlaylistId(playlist1.getId()).get();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPlaylistTitle()).isEqualTo(playlist1.getPlaylistTitle());
    }

    @Test
    void 플레이리스트_삭제(){
        // given
        final User user = userRepository.save(buildUser());
        final Playlist playlist = playlistRepository.save(buildPlaylist(user, "test playlist"));

        // when
        long result = playlistRepository.deletePlaylist(List.of(playlist.getId()));

        // then
        assertThat(result).isEqualTo(1);
    }

    @Test
    void 특정_플리에_트랙이_없다면_플리의_총재생시간은_0으로_리턴() {
        //given
        Playlist playlist = buildPlaylist();
        playlistRepository.save(playlist);

        //when
        int durationTimeSum = playlistRepository.findTotalDurationTimeMsByPlaylistId(
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
        int durationSum = playlistRepository.findTotalDurationTimeMsByPlaylistId(playlist.getId());

        // then
        assertThat(durationSum).isEqualTo(3000L);
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
