package com.team.comma.domain.playlist.recommend.repository;

import static com.team.comma.domain.playlist.recommend.constant.RecommendType.ANONYMOUS;
import static com.team.comma.domain.playlist.recommend.constant.RecommendType.FOLLOWING;
import static org.assertj.core.api.Assertions.assertThat;

import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.playlist.repository.PlaylistRepository;
import com.team.comma.domain.playlist.recommend.constant.RecommendType;
import com.team.comma.domain.playlist.recommend.domain.Recommend;
import com.team.comma.domain.playlist.recommend.dto.RecommendResponse;
import com.team.comma.domain.user.user.constant.UserRole;
import com.team.comma.domain.user.user.constant.UserType;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.profile.domain.UserDetail;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.config.TestConfig;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

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
    void 추천_받은_리스트_조회() {
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
        assertThat(result.size()).isEqualTo(2);
        for (RecommendResponse recommendResponse : result) {
            assertThat(Hibernate.isInitialized(recommendResponse)).isTrue();
        }

    }

    @Test
    void 추천_보낸_리스트_조회() {
        // given
        final User toUser = userRepository.save(buildUser("toUserEmail"));
        final User fromUser = userRepository.save(buildUser("fromUserEmail"));
        final Playlist playlist = playlistRepository.save(buildPlaylist());
        final Recommend recommend = buildRecommendToFollowing(FOLLOWING, playlist, fromUser, toUser);
        final Recommend recommend2 = buildRecommendToFollowing(FOLLOWING, playlist, fromUser, toUser);
        recommendRepository.save(recommend);
        recommendRepository.save(recommend2);

        // when
        final List<RecommendResponse> result = recommendRepository.getRecommendsByFromUser(fromUser);

        // then
        assertThat(result.size()).isEqualTo(2);
        for (RecommendResponse recommendResponse : result) {
            assertThat(Hibernate.isInitialized(recommendResponse)).isTrue();
        }

    }

    @Test
    void 익명_추천_리스트_조회() {
        // given
        final User toUser = userRepository.save(buildUser("toUserEmail"));
        final User fromUser = userRepository.save(buildUser("fromUserEmail"));
        final Playlist playlist = playlistRepository.save(buildPlaylist());
        final Recommend recommend = buildRecommendToFollowing(ANONYMOUS, playlist, fromUser, toUser);
        final Recommend recommend2 = buildRecommendToFollowing(ANONYMOUS, playlist, fromUser, toUser);
        recommendRepository.save(recommend);
        recommendRepository.save(recommend2);

        // when
        final List<RecommendResponse> result = recommendRepository.getRecommendsToAnonymous();

        // then
        assertThat(result.size()).isEqualTo(2);
        for (RecommendResponse recommendResponse : result) {
            assertThat(Hibernate.isInitialized(recommendResponse)).isTrue();
        }

    }

    @Test
    void 추천_정보_id로_찾기() {
        // given
        final User toUser = userRepository.save(buildUser("toUserEmail"));
        final User fromUser = userRepository.save(buildUser("fromUserEmail"));
        final Playlist playlist = playlistRepository.save(buildPlaylist());
        final Recommend recommend = recommendRepository.save(buildRecommendToFollowing(FOLLOWING, playlist, fromUser, toUser));

        // when
        final Optional<Recommend> result = recommendRepository.findById(recommend.getId());

        // then
        assertThat(result.get().getId()).isEqualTo(recommend.getId());
    }

    @Test
    void 추천_플레이리스트_재생횟수_증가() {
        // given
        final User toUser = userRepository.save(buildUser("toUserEmail"));
        final User fromUser = userRepository.save(buildUser("fromUserEmail"));
        final Playlist playlist = playlistRepository.save(buildPlaylist());
        final Recommend recommend = recommendRepository.save(buildRecommendToFollowing(FOLLOWING, playlist, fromUser, toUser));

        // when
        final long result = recommendRepository.increasePlayCount(recommend.getId());

        // then
        assertThat(result).isEqualTo(1);

    }

    Recommend buildRecommendToFollowing(RecommendType type, Playlist playlist, User fromUser, User toUser) {
        return Recommend.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .recommendType(type)
                .comment("test recommend")
                .playlist(playlist)
                .playCount(1L)
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
                .userDetail(UserDetail.builder().profileImageUrl("test").build())
                .build();
    }

    private Playlist buildPlaylist() {
        return Playlist.builder()
                .playlistTitle("My Playlist")
                .alarmFlag(false)
                .build();
    }

}
