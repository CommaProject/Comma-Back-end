package com.team.comma.domain.playlist.recommend.repository;

import static com.team.comma.domain.playlist.recommend.constant.RecommendType.ANONYMOUS;
import static com.team.comma.domain.playlist.recommend.constant.RecommendType.FOLLOWING;
import static org.assertj.core.api.Assertions.assertThat;

import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.playlist.repository.PlaylistRepository;
import com.team.comma.domain.playlist.recommend.constant.RecommendType;
import com.team.comma.domain.playlist.recommend.domain.Recommend;
import com.team.comma.domain.playlist.recommend.dto.RecommendResponse;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.track.repository.TrackRepository;
import com.team.comma.domain.user.detail.repository.UserDetailRepository;
import com.team.comma.domain.user.user.constant.UserRole;
import com.team.comma.domain.user.user.constant.UserType;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.detail.domain.UserDetail;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.config.TestConfig;
import jakarta.persistence.EntityManager;
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
    private UserDetailRepository userDetailRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    EntityManager em;

    @Test
    void 친구에게_추천_저장() {
        // given
        final User toUser = User.createUser("toUserEmail");
        userRepository.save(toUser);

        final User fromUser = User.createUser("fromUserEmail");
        userRepository.save(fromUser);

        final Playlist playlist = Playlist.buildPlaylist(fromUser);
        playlistRepository.save(playlist);

        final Recommend recommend = Recommend.buildRecommend(FOLLOWING, playlist, toUser);

        // when
        final Recommend result = recommendRepository.save(recommend);

        // then
        assertThat(result.getComment()).isEqualTo(recommend.getComment());
    }

    @Test
    void 플레이리스트추천시_카운팅() {
        // given
        final User toUser = User.createUser("toUserEmail");
        final User fromUser = User.createUser("fromUserEmail");
        userRepository.save(toUser);
        userRepository.save(fromUser);

        Track track = trackRepository.save(buildTrack());
        Track track2 = trackRepository.save(buildTrack());
        Playlist playlist = Playlist.buildPlaylist(fromUser);
        playlist.addPlaylistTrack(track);
        playlist.addPlaylistTrack(track2);
        Playlist result = playlistRepository.save(playlist);

        // when
        playlistRepository.updateRecommendCountByPlaylistId(result.getId());
        playlistRepository.updateRecommendCountByPlaylistId(result.getId());
        em.flush();
        em.clear();

        // then
        Track trackResult = trackRepository.findById(track.getId()).get();
        Track trackResult2 = trackRepository.findById(track2.getId()).get();
        assertThat(trackResult.getRecommendCount()).isEqualTo(2);
        assertThat(trackResult2.getRecommendCount()).isEqualTo(2);
    }

    @Test
    void 익명에게_추천_저장() {
        // given
        final User fromUser = userRepository.save(User.createUser());
        final Playlist playlist = playlistRepository.save(Playlist.buildPlaylist(fromUser));
        final Recommend recommend = Recommend.buildRecommend(ANONYMOUS, playlist, null);

        // when
        final Recommend result = recommendRepository.save(recommend);

        // then
        assertThat(result.getComment()).isEqualTo("test recommend");
    }

    @Test
    void 친구에게_추천_중복_여부_체크() {
        // given
        final User toUser = User.createUser("toUserEmail");
        userRepository.save(toUser);

        final User fromUser = User.createUser("fromUserEmail");
        userRepository.save(fromUser);

        final Playlist playlist = Playlist.buildPlaylist(fromUser);
        playlistRepository.save(playlist);

        final Recommend recommend = Recommend.buildRecommend(FOLLOWING, playlist, toUser);
        recommendRepository.save(recommend);

        // when
        final long result = recommendRepository.getRecommendCountByToUserAndPlaylist(recommend);

        // then
        assertThat(result).isEqualTo(1);
    }

    @Test
    void 추천_받은_리스트_조회() {
        // given
        final User toUser = User.createUser("toUserEmail");
        final UserDetail userDetail1 = UserDetail.buildUserDetail(toUser);
        userRepository.save(toUser);
        userDetailRepository.save(userDetail1);

        final User fromUser = User.createUser("fromUserEmail");
        final UserDetail userDetail2 = UserDetail.buildUserDetail(fromUser);
        userRepository.save(fromUser);
        userDetailRepository.save(userDetail2);

        final Playlist playlist = Playlist.buildPlaylist(fromUser);
        playlistRepository.save(playlist);

        final Recommend recommend = Recommend.buildRecommend(FOLLOWING, playlist, toUser);
        final Recommend recommend2 = Recommend.buildRecommend(FOLLOWING, playlist, toUser);
        recommendRepository.save(recommend);
        recommendRepository.save(recommend2);

        // when
        final List<RecommendResponse> result = recommendRepository.getRecommendsByToUser(toUser);

        // then
        assertThat(result.size()).isEqualTo(2);

    }

    @Test
    void 추천_보낸_리스트_조회() {
        // given
        final User toUser = User.createUser("toUserEmail");
        final UserDetail userDetail1 = UserDetail.buildUserDetail(toUser);
        userRepository.save(toUser);
        userDetailRepository.save(userDetail1);

        final User fromUser = User.createUser("fromUserEmail");
        final UserDetail userDetail2 = UserDetail.buildUserDetail(fromUser);
        userRepository.save(fromUser);
        userDetailRepository.save(userDetail2);

        final Playlist playlist = Playlist.buildPlaylist(fromUser);
        playlistRepository.save(playlist);

        final Recommend recommend = Recommend.buildRecommend(FOLLOWING, playlist, toUser);
        final Recommend recommend2 = Recommend.buildRecommend(FOLLOWING, playlist, toUser);
        recommendRepository.save(recommend);
        recommendRepository.save(recommend2);

        // when
        final List<RecommendResponse> result = recommendRepository.getRecommendsByFromUser(fromUser);

        // then
        assertThat(result.size()).isEqualTo(2);

    }

    @Test
    void 익명_추천_리스트_조회() {
        // given
        final User toUser = User.createUser("toUserEmail");
        final User fromUser = User.createUser("fromUserEmail");
        userRepository.save(toUser);
        userRepository.save(fromUser);

        final UserDetail userDetail1 = UserDetail.buildUserDetail(toUser);
        final UserDetail userDetail2 = UserDetail.buildUserDetail(fromUser);
        userDetailRepository.save(userDetail1);
        userDetailRepository.save(userDetail2);

        final Playlist playlist = Playlist.buildPlaylist(fromUser);
        playlistRepository.save(playlist);

        final Recommend recommend = Recommend.buildRecommend(ANONYMOUS, playlist, null);
        final Recommend recommend2 = Recommend.buildRecommend(ANONYMOUS, playlist, null);
        recommendRepository.save(recommend);
        recommendRepository.save(recommend2);

        // when
        final List<RecommendResponse> result = recommendRepository.getRecommendsToAnonymous();

        // then
        assertThat(result.size()).isEqualTo(2);

    }

    @Test
    void 추천_정보_id로_찾기() {
        // given
        final User toUser = userRepository.save(User.createUser("toUserEmail"));
        final User fromUser = userRepository.save(User.createUser("fromUserEmail"));
        final Playlist playlist = playlistRepository.save(Playlist.buildPlaylist(fromUser));
        final Recommend recommend = recommendRepository.save(Recommend.buildRecommend(FOLLOWING, playlist, toUser));

        // when
        final Optional<Recommend> result = recommendRepository.findById(recommend.getId());

        // then
        assertThat(result.get().getId()).isEqualTo(recommend.getId());
    }

    @Test
    void 추천_플레이리스트_재생횟수_증가() {
        // given
        final User toUser = userRepository.save(User.createUser("toUserEmail"));
        final User fromUser = userRepository.save(User.createUser("fromUserEmail"));
        final Playlist playlist = playlistRepository.save(Playlist.buildPlaylist(fromUser));
        final Recommend recommend = recommendRepository.save(Recommend.buildRecommend(FOLLOWING, playlist, toUser));

        // when
        final long result = recommendRepository.increasePlayCount(recommend.getId());

        // then
        assertThat(result).isEqualTo(1);

    }

    Track buildTrack() {
        return Track.builder()
                .trackTitle("title")
                .spotifyTrackHref("href")
                .albumImageUrl("url")
                .spotifyTrackId("id")
                .build();
    }

}
