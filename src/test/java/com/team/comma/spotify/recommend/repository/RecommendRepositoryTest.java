package com.team.comma.spotify.recommend.repository;

import static com.team.comma.spotify.recommend.constant.RecommendType.ANONYMOUS;
import static com.team.comma.spotify.recommend.constant.RecommendType.FOLLOWING;
import static org.assertj.core.api.Assertions.assertThat;

import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.repository.PlaylistRepository;
import com.team.comma.spotify.recommend.constant.RecommendType;
import com.team.comma.spotify.recommend.domain.Recommend;
import com.team.comma.spotify.recommend.dto.RecommendResponse;
import com.team.comma.user.constant.UserRole;
import com.team.comma.user.constant.UserType;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@DataJpaTest
@Import(TestConfig.class)
public class RecommendRepositoryTest {

    @Autowired
    private RecommendRepository recommendRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Test
    void 친구에게_추천_저장() {
        // given
        final User toUser = userRepository.save(buildUser("toUserEmail"));
        final User fromUser = userRepository.save(buildUser("fromUserEmail"));
        final Playlist playlist = playlistRepository.save(buildPlaylist());
        final Recommend recommend = buildRecommendToFollowing(FOLLOWING, playlist, fromUser, toUser);

        // when
        final Recommend result = recommendRepository.save(recommend);

        // then
        assertThat(result.getComment()).isEqualTo("test recommend");
    }

    @Test
    void 익명에게_추천_저장() {
        // given
        final User fromUser = userRepository.save(buildUser("fromUserEmail"));
        final Playlist playlist = playlistRepository.save(buildPlaylist());
        final Recommend recommend = buildRecommendToAnonymous(ANONYMOUS, playlist, fromUser);

        // when
        final Recommend result = recommendRepository.save(recommend);

        // then
        assertThat(result.getComment()).isEqualTo("test recommend");
    }

    @Test
    void 친구에게_추천_중복_여부_체크() {
        // given
        final User toUser = userRepository.save(buildUser("toUserEmail"));
        final User fromUser = userRepository.save(buildUser("fromUserEmail"));
        final Playlist playlist = playlistRepository.save(buildPlaylist());
        final Recommend recommend = buildRecommendToFollowing(FOLLOWING, playlist, fromUser, toUser);
        recommendRepository.save(recommend);

        // when
        final long result = recommendRepository.getRecommendCountByToUserAndPlaylist(recommend);

        // then
        assertThat(result).isEqualTo(1);
    }

    @Test
    void 추천_받았던_리스트_조회_2개() {
        // given
        final User toUser = userRepository.save(buildUser("toUserEmail"));
        final User fromUser = userRepository.save(buildUser("fromUserEmail"));
        final Playlist playlist = playlistRepository.save(buildPlaylist());
        final Recommend recommend = buildRecommendToFollowing(FOLLOWING, playlist, fromUser, toUser);
        final Recommend recommend2 = buildRecommendToFollowing(FOLLOWING, playlist, fromUser, toUser);
        recommendRepository.save(recommend);
        recommendRepository.save(recommend2);

        // when
        final List<RecommendResponse> result = recommendRepository.getRecommendsByToUser(toUser);

        // then
        assertThat(result).size().isEqualTo(2);

    }

    Recommend buildRecommendToFollowing(RecommendType type, Playlist playlist, User fromUser, User toUser) {
        return Recommend.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .recommendType(type)
                .comment("test recommend")
                .playlist(playlist)
                .build();
    }

    Recommend buildRecommendToAnonymous(RecommendType type, Playlist playlist, User fromUser) {
        return Recommend.builder()
                .fromUser(fromUser)
                .recommendType(type)
                .comment("test recommend")
                .playlist(playlist)
                .build();
    }

    private User buildUser(String toUserEmail) {
        return User.builder()
                .email(toUserEmail)
                .type(UserType.GENERAL_USER)
                .role(UserRole.USER)
                .build();
    }

    private Playlist buildPlaylist() {
        return Playlist.builder()
                .playlistTitle("My Playlist")
                .alarmFlag(false)
                .build();
    }

}
