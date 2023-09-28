package com.team.comma.domain.favorite.artist.repository;

import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.artist.repository.ArtistRepository;
import com.team.comma.domain.favorite.artist.domain.FavoriteArtist;
import com.team.comma.domain.favorite.artist.dto.FavoriteArtistResponse;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FavoriteArtistRepositoryTest {

    @Autowired
    FavoriteArtistRepository favoriteArtistRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArtistRepository artistRepository;

    private String userEmail = "email@naver.com";
    private String userPassword = "password";

    @Test
    @DisplayName("관심 아티스트 추가")
    public void addFavoriteArtist() {
        // given
        User user = User.builder().build();
        Artist artist = Artist.buildArtist();
        FavoriteArtist favoriteArtist = FavoriteArtist.builder().artist(artist).user(user).build();

        // when
        FavoriteArtist result = favoriteArtistRepository.save(favoriteArtist);

        // then
        assertThat(result.getArtist().getArtistName()).isEqualTo("artistName");
    }

    @Test
    @DisplayName("사용자 관심 아티스트 삭제")
    public void deleteFavoriteArtist() {
        // given
        User userEntity = User.builder().email("email").build();
        userRepository.save(userEntity);

        Artist artist = Artist.buildArtist();
        FavoriteArtist favoriteArtist = FavoriteArtist.buildFavoriteArtist(userEntity, artist);
        favoriteArtist = favoriteArtistRepository.save(favoriteArtist);

        // when
        favoriteArtistRepository.deleteById(favoriteArtist.getId());

        // then
        List<FavoriteArtistResponse> result = favoriteArtistRepository.findAllFavoriteArtistByUser(userEntity);
        assertThat(result.size()).isEqualTo(0);

    }

    @Test
    @DisplayName("사용자 관심 아티스트 가져오기")
    public void getFavoriteArtistRepository() {
        // given
        User user = buildUser();
        Artist artist1 = Artist.buildArtist();
        Artist artist2 = Artist.buildArtist();
        Artist artist3 = Artist.buildArtist();
        user.addFavoriteArtist(artist1);
        user.addFavoriteArtist(artist2);
        user.addFavoriteArtist(artist3);
        artistRepository.save(artist1);
        artistRepository.save(artist2);
        artistRepository.save(artist3);
        userRepository.save(user);

        // when
        List<String> result = favoriteArtistRepository.findFavoriteArtistListByUser(user);

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("유저가 추가한 하나의 아티스트 가져오기")
    public void getFavoriteArtistByUser() {
        // given
        Artist artist = Artist.buildArtist();
        artistRepository.save(artist);

        User user = buildUser();
        userRepository.save(user);
        user.addFavoriteArtist(artist);

        // when
        Optional<FavoriteArtist> result = favoriteArtistRepository.findFavoriteArtistByUser(user , "artist1");

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("아티스트 좋아요 리스트 조회")
    public void findAllByUser() {
        // given
        User user = buildUser();
        Artist artist = Artist.buildArtist();
        FavoriteArtist favoriteArtist = FavoriteArtist.buildFavoriteArtist(user, artist);
        userRepository.save(user);
        artistRepository.save(artist);
        favoriteArtistRepository.save(favoriteArtist);

        // when
        List<FavoriteArtistResponse> result = favoriteArtistRepository.findAllFavoriteArtistByUser(user);

        // then
        assertThat(result).isNotNull();

    }

    private User buildUser() {
        return User.builder()
                .email(userEmail)
                .password(userPassword)
                .type(UserType.GENERAL_USER)
                .role(UserRole.USER)
                .build();
    }

}
