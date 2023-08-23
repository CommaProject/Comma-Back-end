package com.team.comma.domain.spotify.track.repository;

import com.team.comma.domain.user.following.domain.Following;
import com.team.comma.domain.user.following.repository.FollowingRepository;
import com.team.comma.domain.track.trackPlayCount.TrackPlayCount;
import com.team.comma.domain.track.track.dto.TrackPlayCountRequest;
import com.team.comma.domain.track.track.dto.TrackPlayCountResponse;
import com.team.comma.domain.track.track.repository.count.TrackPlayCountRepository;
import com.team.comma.domain.user.user.constant.UserRole;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.global.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.team.comma.domain.user.following.domain.Following.createFollowingToFrom;
import static com.team.comma.domain.track.trackPlayCount.TrackPlayCount.createTrackPlayCount;
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

        Following following1 = createFollowingToFrom(friend1 , user);
        Following following2 = createFollowingToFrom(friend2 , user);
        followingRepository.save(following1);
        followingRepository.save(following2);

        // when
        List<TrackPlayCountResponse> result = trackPlayCountRepository.findTrackPlayCountByFriend("email");

        // then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("내가 가장 많이 들은 곡")
    public void findTrackPlayCountByMostListenedTrack() {
        // given
        User user = buildUser();
        trackPlayCountRepository.save(buildTrackPlayCount(4 , user));
        trackPlayCountRepository.save(buildTrackPlayCount(3 , user));
        trackPlayCountRepository.save(buildTrackPlayCount(1 , user));

        // when
        List<TrackPlayCountResponse> result = trackPlayCountRepository.findTrackPlayCountByMostListenedTrack("email");

        // then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).getPlayCount()).isEqualTo(4);
        assertThat(result.get(1).getPlayCount()).isEqualTo(3);
        assertThat(result.get(2).getPlayCount()).isEqualTo(1);
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

    public TrackPlayCount buildTrackPlayCount(int count , User user) {
        return TrackPlayCount.builder()
                .playCount(count)
                .trackArtist("artist")
                .trackName("name")
                .trackId("id")
                .user(user)
                .trackImageUrl("url")
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
