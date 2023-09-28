package com.team.comma.domain.favorite.track.repository;

import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.artist.repository.ArtistRepository;
import com.team.comma.domain.favorite.track.domain.FavoriteTrack;
import com.team.comma.domain.favorite.track.dto.FavoriteTrackResponse;
import com.team.comma.domain.track.artist.repository.TrackArtistRepository;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.artist.dto.TrackArtistResponse;
import com.team.comma.domain.track.track.repository.TrackRepository;
import com.team.comma.domain.user.user.constant.UserRole;
import com.team.comma.domain.user.user.constant.UserType;
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
import java.util.Optional;

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
        User user = User.createUser("email", "password", UserType.GENERAL_USER);
        userRepository.save(user);

        Track track = Track.buildTrack();
        trackRepository.save(track);

        FavoriteTrack favoriteTrack = FavoriteTrack.createFavoriteTrack(user, track);

        // when
        FavoriteTrack result = favoriteTrackRepository.save(favoriteTrack);

        // then
        assertThat(result.getTrack().getTrackTitle()).isEqualTo(track.getTrackTitle());

    }

    @Test
    @DisplayName("트랙 좋아요 리스트 조회")
    void findAllByUser() {
        // given
        User user = User.createUser("email", "password", UserType.GENERAL_USER);
        userRepository.save(user);

        Artist artist = Artist.buildArtist();
        artistRepository.save(artist);

        Track track = Track.buildTrack();
        track.addTrackArtistList(artist);
        trackRepository.save(track);

        FavoriteTrack favoriteTrack1 = FavoriteTrack.createFavoriteTrack(user, track);
        FavoriteTrack favoriteTrack2 = FavoriteTrack.createFavoriteTrack(user, track);
        FavoriteTrack favoriteTrack3 = FavoriteTrack.createFavoriteTrack(user, track);
        favoriteTrackRepository.save(favoriteTrack1);
        favoriteTrackRepository.save(favoriteTrack2);
        favoriteTrackRepository.save(favoriteTrack3);

        // when
        List<FavoriteTrackResponse> result = favoriteTrackRepository.findAllFavoriteTrackByUser(user);

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    void deleteFavoriteTrack() {
        // given
        User user = User.createUser("email", "password", UserType.GENERAL_USER);
        userRepository.save(user);

        Track track = Track.buildTrack();
        trackRepository.save(track);

        FavoriteTrack favoriteTrack = FavoriteTrack.createFavoriteTrack(user, track);
        favoriteTrackRepository.save(favoriteTrack);

        // when
        favoriteTrackRepository.delete(favoriteTrack);

        // then
        Optional<FavoriteTrack> result = favoriteTrackRepository.findById(favoriteTrack.getId());
        assertThat(result).isNotPresent();

    }

}
