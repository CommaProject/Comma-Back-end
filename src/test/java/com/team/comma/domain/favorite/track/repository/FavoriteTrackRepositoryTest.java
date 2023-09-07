package com.team.comma.domain.favorite.track.repository;

import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.artist.repository.ArtistRepository;
import com.team.comma.domain.favorite.track.domain.FavoriteTrack;
import com.team.comma.domain.favorite.track.dto.FavoriteTrackResponse;
import com.team.comma.domain.track.artist.repository.TrackArtistRepository;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.track.dto.TrackArtistResponse;
import com.team.comma.domain.track.track.repository.TrackRepository;
import com.team.comma.domain.user.user.constant.UserRole;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FavoriteTrackRepositoryTest {

    private String spotifyTrackId = "input ISRC of track";
    
    @Autowired
    FavoriteTrackRepository favoriteTrackRepository;

    @Autowired
    TrackRepository trackRepository;

    @Autowired
    TrackArtistRepository trackArtistRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Test
    @DisplayName("트랙 좋아요 추가")
    void saveFavoriteTrack() {
        // given
        User user = userRepository.save(buildUser());
        Track track = buildTrack("track title");
        FavoriteTrack favoriteTrack = FavoriteTrack.buildFavoriteTrack(user, track);

        // when
        FavoriteTrack result = favoriteTrackRepository.save(favoriteTrack);

        // then
        assertThat(result.getTrack().getTrackTitle()).isEqualTo("track title");

    }

    @Test
    @DisplayName("좋아하는 트랙을 이메일로 조회")
    void findFavoriteTrackByEmail() {
        // given
        User user = userRepository.save(buildUser());
        Track track1 = buildTrack("track title1");
        Track track2 = buildTrack("track title2");
        Track track3 = buildTrack("track title3");
        Artist artist = Artist.builder().artistName("artist").build();
        artistRepository.save(artist);
        track1.addTrackArtistList(artist);
        trackRepository.save(track1);
        track2.addTrackArtistList(artist);
        trackRepository.save(track2);
        track2.addTrackArtistList(artist);
        trackRepository.save(track3);

        FavoriteTrack favoriteTrack1 = FavoriteTrack.buildFavoriteTrack(user, track1);
        FavoriteTrack favoriteTrack2 = FavoriteTrack.buildFavoriteTrack(user, track2);
        FavoriteTrack favoriteTrack3 = FavoriteTrack.buildFavoriteTrack(user, track3);

        favoriteTrackRepository.save(favoriteTrack1);
        favoriteTrackRepository.save(favoriteTrack2);
        favoriteTrackRepository.save(favoriteTrack3);

        // when
        List<TrackArtistResponse> result = favoriteTrackRepository.findFavoriteTrackByEmail("email");
        
        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("트랙 좋아요 리스트 조회")
    void findAllByUser() {
        // given
        User user = userRepository.save(buildUser());
        Track track = trackRepository.save(buildTrack("track title"));
        Artist artist = artistRepository.save(buildArtist());
        track.addTrackArtistList(artist);
        FavoriteTrack favoriteTrack1 = FavoriteTrack.buildFavoriteTrack(user, track);
        FavoriteTrack favoriteTrack2 = FavoriteTrack.buildFavoriteTrack(user, track);
        FavoriteTrack favoriteTrack3 = FavoriteTrack.buildFavoriteTrack(user, track);
        favoriteTrackRepository.save(favoriteTrack1);
        favoriteTrackRepository.save(favoriteTrack2);
        favoriteTrackRepository.save(favoriteTrack3);

        // when
        List<FavoriteTrackResponse> result = favoriteTrackRepository.findAllFavoriteTrackByUser(user);

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    private Track buildTrack(String title) {
        return Track.builder()
                .trackTitle(title)
                .recommendCount(0L)
                .albumImageUrl("url")
                .spotifyTrackHref("spotifyTrackHref")
                .spotifyTrackId(spotifyTrackId)
                .build();
    }

    public Artist buildArtist() {
        return Artist.builder()
                .artistName("artist")
                .build();
    }

    public User buildUser() {
        return User.builder()
                .email("email")
                .password("password")
                .role(UserRole.USER)
                .build();
    }

}
