package com.team.comma.domain.track.track.repository;

import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.artist.repository.ArtistRepository;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.track.dto.TrackArtistResponse;
import com.team.comma.global.config.TestConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TrackRepositoryTest {

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ArtistRepository artistRepository;

    private String spotifyTrackId = "spotifyTrackId";

    @Test
    void 곡_저장() {
        // given

        // when
        final Track result = trackRepository.save(buildTrack("test track" , spotifyTrackId));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTrackTitle()).isEqualTo("test track");
    }

    @Test
    void 곡_조회_실패_곡정보없음() {
        // given

        // when
        final Optional<Track> result = trackRepository.findById(123L);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void 곡_조회_성공() {
        // given
        final Track track = trackRepository.save(buildTrack("test track" , spotifyTrackId));

        // when
        final Optional<Track> result = trackRepository.findById(track.getId());

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).isEqualTo(Optional.of(track));
    }

    @Test
    void spotify_track_id로_곡_조회_성공() {
        // given
        final Track track = trackRepository.save(buildTrack("test track" , spotifyTrackId));

        // when
        final Optional<Track> result = trackRepository.findBySpotifyTrackId(spotifyTrackId);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).isEqualTo(Optional.of(track));
    }

    @Test
    void spotify_track_id로_곡_조회_실패_곡정보없음() {
        // given

        // when
        final Optional<Track> result = trackRepository.findBySpotifyTrackId(spotifyTrackId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("트랙 추천 횟수 증가")
    void updateTrackRecommendCount() {
        // given
        Track tracks = buildTrack("track" , spotifyTrackId);
        trackRepository.save(tracks);
        entityManager.flush();
        entityManager.clear();

        for (int i = 0; i < 3; i++) {
            trackRepository.updateTrackRecommendCount(spotifyTrackId);
        }

        // when
        Track result = trackRepository.findBySpotifyTrackId(spotifyTrackId).get();

        // then
        assertThat(result.getRecommendCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("추천 갯수 별 트랙 조회")
    void findTrackMostRecommended() {
        // given
        Track tracks1 = buildTrack("track1" , "id1");
        Track tracks2 = buildTrack("track2" , "id2");
        Artist artist = Artist.builder().artistId("artistId").artistName("artist").build();
        artistRepository.save(artist);
        
        tracks1.addTrackArtistList(artist);
        tracks2.addTrackArtistList(artist);
        trackRepository.save(tracks1);
        trackRepository.save(tracks2);
        entityManager.flush();
        entityManager.clear();

        trackRepository.updateTrackRecommendCount("id1");
        trackRepository.updateTrackRecommendCount("id2");
        trackRepository.updateTrackRecommendCount("id2");

        // when
        List<TrackArtistResponse> result = trackRepository.findTrackMostRecommended();

        // then
        assertThat(result.size()).isEqualTo(2);
    }


    private Track buildTrack(String title , String spotifyId) {
        return Track.builder()
                .trackTitle(title)
                .recommendCount(0L)
                .albumImageUrl("url")
                .spotifyTrackHref("spotifyTrackHref")
                .spotifyTrackId(spotifyId)
                .build();
    }


}
