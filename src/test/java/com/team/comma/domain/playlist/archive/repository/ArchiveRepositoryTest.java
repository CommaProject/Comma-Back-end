package com.team.comma.domain.playlist.archive.repository;

import com.team.comma.domain.playlist.archive.domain.Archive;
import com.team.comma.domain.playlist.archive.dto.ArchiveResponse;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.playlist.repository.PlaylistRepository;
import com.team.comma.domain.playlist.track.domain.PlaylistTrack;
import com.team.comma.domain.playlist.track.repository.PlaylistTrackRepository;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.track.repository.TrackRepository;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ArchiveRepositoryTest {

    @Autowired
    ArchiveRepository archiveRepository;

    @Autowired
    PlaylistRepository playlistRepository;

    @Autowired
    TrackRepository trackRepository;

    @Autowired
    PlaylistTrackRepository playlistTrackRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("아카이브 정보 저장")
    public void saveArchive() {
        // given
        User user = User.builder().build();
        Playlist playlist = Playlist.builder().build();
        Archive archive = Archive.buildArchive(user, "content", playlist);

        // when
        Archive result = archiveRepository.save(archive);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getComment()).isEqualTo("content");
    }

    @Test
    @DisplayName("아카이브 날짜로 조회")
    public void findAllArchiveByDate() {
        // given
        User user = userRepository.save(User.buildUser());
        Track track = trackRepository.save(buildTrack());
        Playlist playlist = playlistRepository.save(Playlist.buildPlaylist(user));
        PlaylistTrack playlistTrack = playlistTrackRepository.save(PlaylistTrack.builder().playlist(playlist).track(track).build());
        Archive archive = archiveRepository.save(Archive.buildArchive(user, "content", playlist));

        LocalDateTime startDateTime = LocalDate.of(2023,8,1).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.now().atTime(LocalTime.MAX);

        // when
        List<ArchiveResponse> result = archiveRepository.findArchiveByDateTime(user, startDateTime, endDateTime);
        System.out.println(result);
        System.out.println(startDateTime);
        System.out.println(endDateTime);

        // then
        assertThat(result.size()).isEqualTo(1);
    }

    public Track buildTrack(){
        return Track.builder()
                .trackTitle("title")
                .albumImageUrl("url")
                .spotifyTrackHref("href")
                .spotifyTrackId("id123")
                .build();
    }
}
