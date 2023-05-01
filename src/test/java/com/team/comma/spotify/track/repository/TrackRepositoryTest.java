package com.team.comma.spotify.track.repository;

import static org.assertj.core.api.Assertions.assertThat;  //자동 import되지 않음

import com.team.comma.spotify.track.domain.Track;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TrackRepositoryTest {

    @Autowired
    private TrackRepository trackRepository;

    private final String title = "test track";

    @Test
    public void 곡_저장(){
        // given

        // when
        final Track result = trackRepository.save(getTrack(title));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTrackTitle()).isEqualTo(title);
    }

    @Test
    public void 곡_조회_실패_곡정보없음(){
        // given

        // when
        final Optional<Track> result = trackRepository.findById(123L);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    public void 곡_조회_성공(){
        // given
        final Track track = trackRepository.save(getTrack("track test"));

        // when
        final Optional<Track> result = trackRepository.findById(track.getId());

        // then
        assertThat(result).isNotEmpty();
    }

    private Track getTrack(String title) {
        return Track.builder()
                .trackTitle(title)
                .build();
    }

}
