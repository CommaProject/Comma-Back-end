package com.team.comma.domain.favorite.repository;

import com.team.comma.domain.favorite.domain.FavoriteTrack;
import com.team.comma.domain.track.domain.Track;
import com.team.comma.domain.favorite.repository.FavoriteTrackRepository;
import com.team.comma.domain.user.constant.UserRole;
import com.team.comma.domain.user.domain.User;
import com.team.comma.global.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.team.comma.domain.favorite.domain.FavoriteTrack.createFavoriteTrack;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FavoriteTrackRepositoryTest {

    private String spotifyTrackId = "input ISRC of track";
    
    @Autowired
    FavoriteTrackRepository favoriteTrackRepository;

    @Test
    @DisplayName("좋아하는 트랙을 이메일로 조회")
    void findTrackByEmail() {
        // given
        FavoriteTrack track1 = createFavoriteTrack(buildUser() , buildTrack("title1"));
        FavoriteTrack track2 = createFavoriteTrack(buildUser() , buildTrack("title2"));
        FavoriteTrack track3 = createFavoriteTrack(buildUser() , buildTrack("title3"));

        favoriteTrackRepository.save(track1);
        favoriteTrackRepository.save(track2);
        favoriteTrackRepository.save(track3);

        // when
        List<Track> result = favoriteTrackRepository.findFavoriteTrackByEmail("email");
        
        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("트랙 좋아요")
    void favoriteTrack() {
        // given
        FavoriteTrack track1 = createFavoriteTrack(buildUser() , buildTrack("title1"));

        // when
        FavoriteTrack result = favoriteTrackRepository.save(track1);

        // then
        assertThat(result.getTrack().getTrackTitle()).isEqualTo("title1");

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
