package com.team.comma.domain.favorite.track.repository;

import com.team.comma.domain.favorite.track.domain.FavoriteTrack;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.favorite.track.repository.FavoriteTrackRepository;
import com.team.comma.domain.user.user.constant.UserRole;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.favorite.domain.FavoriteTrack;
import com.team.comma.domain.track.domain.Track;
import com.team.comma.domain.user.constant.UserRole;
import com.team.comma.domain.user.domain.User;
import com.team.comma.domain.user.repository.user.UserRepository;
import com.team.comma.global.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.team.comma.domain.favorite.track.domain.FavoriteTrack.createFavoriteTrack;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FavoriteTrackRepositoryTest {

    private String spotifyTrackId = "input ISRC of track";
    
    @Autowired
    FavoriteTrackRepository favoriteTrackRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("트랙 좋아요 추가")
    void saveFavoriteTrack() {
        // given
        FavoriteTrack track1 = FavoriteTrack.buildFavoriteTrack(buildUser() , buildTrack("title1"));

        // when
        FavoriteTrack result = favoriteTrackRepository.save(track1);

        // then
        assertThat(result.getTrack().getTrackTitle()).isEqualTo("title1");

    }

    @Test
    @DisplayName("좋아하는 트랙을 이메일로 조회")
    void findFavoriteTrackByEmail() {
        // given
        FavoriteTrack favoriteTrack1 = FavoriteTrack.buildFavoriteTrack(buildUser() , buildTrack("title1"));
        FavoriteTrack favoriteTrack2 = FavoriteTrack.buildFavoriteTrack(buildUser() , buildTrack("title2"));
        FavoriteTrack favoriteTrack3 = FavoriteTrack.buildFavoriteTrack(buildUser() , buildTrack("title3"));

        favoriteTrackRepository.save(favoriteTrack1);
        favoriteTrackRepository.save(favoriteTrack2);
        favoriteTrackRepository.save(favoriteTrack3);

        // when
        List<Track> result = favoriteTrackRepository.findFavoriteTrackByEmail("email");
        
        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("트랙 좋아요 리스트 조회")
    void findAllByUser() {
        // given
        User user = userRepository.save(buildUser());

        FavoriteTrack favoriteTrack1 = FavoriteTrack.buildFavoriteTrack(user, buildTrack("title1"));
        FavoriteTrack favoriteTrack2 = FavoriteTrack.buildFavoriteTrack(user, buildTrack("title2"));
        FavoriteTrack favoriteTrack3 = FavoriteTrack.buildFavoriteTrack(user, buildTrack("title3"));
        favoriteTrackRepository.save(favoriteTrack1);
        favoriteTrackRepository.save(favoriteTrack2);
        favoriteTrackRepository.save(favoriteTrack3);

        // when
        List<FavoriteTrack> result = favoriteTrackRepository.findAllByUser(user);

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

    public User buildUser() {
        return User.builder()
                .email("email")
                .password("password")
                .role(UserRole.USER)
                .build();
    }

}
