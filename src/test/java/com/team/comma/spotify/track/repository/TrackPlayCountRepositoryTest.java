package com.team.comma.spotify.track.repository;

import com.team.comma.follow.domain.Following;
import com.team.comma.follow.repository.FollowingRepository;
import com.team.comma.spotify.track.domain.TrackPlayCount;
import com.team.comma.spotify.track.dto.TrackPlayCountRequest;
import com.team.comma.spotify.track.repository.count.TrackPlayCountRepository;
import com.team.comma.user.constant.UserRole;
import com.team.comma.user.domain.User;
import com.team.comma.util.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.team.comma.follow.domain.Following.createFollowing;
import static com.team.comma.spotify.track.domain.TrackPlayCount.createTrackPlayCount;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TrackPlayCountRepositoryTest {

    @Autowired
    public TrackPlayCountRepository trackPlayCountRepository;

    @Autowired
    public FollowingRepository followingRepository;

    @Test
    @DisplayName("유저 이메일과 트랙 Id 로 탐색")
    public void findTrackPlayCountByUserEmail() {
        // given
        User user = buildUser();
        TrackPlayCountRequest trackPlayCountRequest = buildTrackPlayCount();
        TrackPlayCount trackPlayCount = createTrackPlayCount(trackPlayCountRequest, user);

        trackPlayCountRepository.save(trackPlayCount);

        // when
        TrackPlayCount result = trackPlayCountRepository.findTrackPlayCountByUserEmail("email" , "trackId").get();

        // then
        assertThat(result.getPlayCount()).isEqualTo(1);
        assertThat(result.getTrackId()).isEqualTo("trackId");
    }

    @Test
    @DisplayName("친구가 가장 많이 들은 노래 탐색")
    public void findTrackPlayCountByFriend() {
        // given
        User user = buildUser();
        User friend1 = buildUser();
        User friend2 = buildUser();
        TrackPlayCountRequest trackPlayCountRequest = buildTrackPlayCount();
        TrackPlayCount trackPlayCount = createTrackPlayCount(trackPlayCountRequest, user);
        TrackPlayCount trackPlayCount1 = createTrackPlayCount(trackPlayCountRequest, friend1);
        TrackPlayCount trackPlayCount2 = createTrackPlayCount(trackPlayCountRequest, friend2);

        trackPlayCountRepository.save(trackPlayCount);
        trackPlayCountRepository.save(trackPlayCount1);
        trackPlayCountRepository.save(trackPlayCount2);

        Following following1 = createFollowing(friend1 , user);
        Following following2 = createFollowing(friend2 , user);
        followingRepository.save(following1);
        followingRepository.save(following2);

        // when
        List<TrackPlayCount> result = trackPlayCountRepository.findTrackPlayCountByFriend("email");

        // then
        assertThat(result.size()).isEqualTo(2);
    }

    public TrackPlayCountRequest buildTrackPlayCount() {
        return TrackPlayCountRequest.builder()
                .trackArtist("artist")
                .trackImageUrl("image")
                .trackId("trackId")
                .trackName("name")
                .trackName("track")
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
