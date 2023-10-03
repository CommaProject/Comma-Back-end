package com.team.comma.domain.track.playcount.repository;

import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.artist.repository.ArtistRepository;
import com.team.comma.domain.track.artist.domain.TrackArtist;
import com.team.comma.domain.track.playcount.domain.TrackPlayCount;
import com.team.comma.domain.track.playcount.dto.TrackPlayCountResponse;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.track.repository.TrackRepository;
import com.team.comma.domain.user.detail.domain.UserDetail;
import com.team.comma.domain.user.following.domain.Following;
import com.team.comma.domain.user.following.repository.FollowingRepository;
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

import java.util.ArrayList;
import java.util.List;

import static com.team.comma.domain.track.playcount.domain.TrackPlayCount.createTrackPlayCount;
import static com.team.comma.domain.user.following.domain.Following.createFollowingToFrom;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.ARRAY;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TrackPlayCountRepositoryTest {

    @Autowired
    public TrackPlayCountRepository trackPlayCountRepository;

    @Autowired
    public FollowingRepository followingRepository;

    @Autowired
    public TrackRepository trackRepository;

    @Autowired
    public ArtistRepository artistRepository;

    @Autowired
    public UserRepository userRepository;

    @Test
    @DisplayName("친구가 가장 많이 들은 노래 탐색")
    public void findTrackPlayCountByFriend() {
        // given
        User user = User.createUser("email");
        userRepository.save(user);
        User friend1 = User.createUser("friend1");
        userRepository.save(user);
        User friend2 = User.createUser("friend2");
        userRepository.save(user);

        Following following1 = createFollowingToFrom(friend1 , user);
        Following following2 = createFollowingToFrom(friend2 , user);
        followingRepository.save(following1);
        followingRepository.save(following2);

        Artist artist = Artist.buildArtist();
        Track track = Track.buildTrack();
        track.addTrackArtistList(artist);
        artistRepository.save(artist);
        trackRepository.save(track);

        TrackPlayCount trackPlayCount1 = TrackPlayCount.createTrackPlayCount(track, user);
        TrackPlayCount trackPlayCount2 = TrackPlayCount.createTrackPlayCount(track, friend1);
        TrackPlayCount trackPlayCount3 = TrackPlayCount.createTrackPlayCount(track, friend2);
        trackPlayCountRepository.save(trackPlayCount1);
        trackPlayCountRepository.save(trackPlayCount2);
        trackPlayCountRepository.save(trackPlayCount3);

        // when
        List<TrackPlayCountResponse> result = trackPlayCountRepository.findTrackPlayCountByFriend("email");

        // then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("내가 가장 많이 들은 곡")
    public void findTrackPlayCountByMostListenedTrack() {
        // given
        final User user = User.createUser();
        userRepository.save(user);

        Artist artist = Artist.buildArtist();
        Track track = Track.buildTrack();
        track.addTrackArtistList(artist);
        artistRepository.save(artist);
        trackRepository.save(track);

        TrackPlayCount trackPlayCount1 = TrackPlayCount.createTrackPlayCount(track, user);
        TrackPlayCount trackPlayCount2 = TrackPlayCount.createTrackPlayCount(track, user);
        TrackPlayCount trackPlayCount3 = TrackPlayCount.createTrackPlayCount(track, user);
        trackPlayCountRepository.save(trackPlayCount1);
        trackPlayCountRepository.save(trackPlayCount2);
        trackPlayCountRepository.save(trackPlayCount3);

        // when
        List<TrackPlayCountResponse> result = trackPlayCountRepository.findTrackPlayCountByMostListenedTrack("email");

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getPlayCount()).isEqualTo(3);
    }

}
