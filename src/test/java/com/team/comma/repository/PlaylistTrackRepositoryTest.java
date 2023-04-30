package com.team.comma.repository;

import com.team.comma.constant.UserRole;
import com.team.comma.constant.UserType;
import com.team.comma.domain.Playlist;
import com.team.comma.domain.PlaylistTrack;
import com.team.comma.domain.Track;
import com.team.comma.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;  //자동 import되지 않음

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PlaylistTrackRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private PlaylistTrackRepository playlistTrackRepository;

    private final String userEmail = "email@naver.com";

    private final String title = "test playlist";

    private final String trackTitle = "test track";

    @Test
    public void 플레이리스트_곡_연관관계_저장(){
        // given
        final User user = userRepository.save(getUser());
        final Playlist playlist = playlistRepository.save(getPlaylist(user, title));
        final Track track = trackRepository.save(getTrack(trackTitle));

        // when
        final PlaylistTrack result = playlistTrackRepository.save(getPlaylistTrack(playlist,track));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPlaylist().getId()).isEqualTo(playlist.getId());
        assertThat(result.getTrack().getId()).isEqualTo(track.getId());
    }

    @Test
    public void 플레이리스트_곡_연관관계_조회_0개(){
        // given
        final User user = userRepository.save(getUser());
        final Playlist playlist = playlistRepository.save(getPlaylist(user, title));

        // when
        final List<PlaylistTrack> result = playlistTrackRepository.findAllByPlaylist_Id(playlist.getId());

        // then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void 플레이리스트_곡_연관관계_조회_2개(){
        // given
        final User user = userRepository.save(getUser());
        final Playlist playlist = playlistRepository.save(getPlaylist(user, title));
        final Track track1 = trackRepository.save(getTrack(trackTitle));
        final Track track2 = trackRepository.save(getTrack(trackTitle));
        playlistTrackRepository.save(getPlaylistTrack(playlist,track1));
        playlistTrackRepository.save(getPlaylistTrack(playlist,track2));

        // when
        final List<PlaylistTrack> result = playlistTrackRepository.findAllByPlaylist_Id(playlist.getId());

        // then
        assertThat(result.size()).isEqualTo(2);
    }

    private User getUser() {
        return User.builder()
                .email(userEmail)
                .type(UserType.GeneralUser)
                .role(UserRole.USER)
                .build();
    }

    private Playlist getPlaylist(User user, String title) {
        return Playlist.builder()
                .playlistTitle(title)
                .alarmFlag(true)
                .user(user)
                .build();
    }

    private Track getTrack(String title) {
        return Track.builder()
                .trackTitle(title)
                .build();
    }

    private PlaylistTrack getPlaylistTrack(Playlist playlist,Track track) {
        return PlaylistTrack.builder()
                .playlist(playlist)
                .track(track)
                .build();
    }
}
