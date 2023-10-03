package com.team.comma.domain.track.playcount.service;

import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.artist.dto.ArtistResponse;
import com.team.comma.domain.track.artist.dto.TrackArtistResponse;
import com.team.comma.domain.track.playcount.dto.TrackPlayCountResponse;
import com.team.comma.domain.track.playcount.repository.TrackPlayCountRepository;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.track.dto.TrackResponse;
import com.team.comma.global.message.MessageResponse;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.team.comma.global.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class PlayCountServiceTest {

    @InjectMocks
    PlayCountService playCountService;

    @Mock
    TrackPlayCountRepository trackPlayCountRepository;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("내가 가장 많이 들은 곡")
    void findMostListenedTrack() {
        // given
        Artist artist = Artist.buildArtist();
        Track track = Track.buildTrack();

        ArtistResponse artistResponse = ArtistResponse.of(artist);
        TrackResponse trackResponse = TrackResponse.of(track);
        TrackArtistResponse trackArtistResponse = TrackArtistResponse.of(trackResponse, artistResponse);
        TrackPlayCountResponse trackPlayCount = TrackPlayCountResponse.of(100, trackArtistResponse);

        doReturn(List.of(trackPlayCount)).when(trackPlayCountRepository).findTrackPlayCountByMostListenedTrack(any(String.class));
        doReturn("userEmail").when(jwtTokenProvider).getUserPk("token");

        // when
        MessageResponse result = playCountService.findMostListenedTrack("token");

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
        assertThat(((List<TrackPlayCountResponse>) result.getData())).size().isEqualTo(1);
        assertThat(((List<TrackPlayCountResponse>) result.getData()).get(0).getPlayCount()).isEqualTo(100);
    }

    @Test
    @DisplayName("친구가 가장 많이 들은 곡")
    void findMostListenedTrackByFriend() {
        // given
        Artist artist = Artist.buildArtist();
        Track track = Track.buildTrack();

        ArtistResponse artistResponse = ArtistResponse.of(artist);
        TrackResponse trackResponse = TrackResponse.of(track);
        TrackArtistResponse trackArtistResponse = TrackArtistResponse.of(trackResponse, artistResponse);
        TrackPlayCountResponse trackPlayCount = TrackPlayCountResponse.of(100, trackArtistResponse);

        doReturn(List.of(trackPlayCount)).when(trackPlayCountRepository).findTrackPlayCountByFriend(any(String.class));
        doReturn("userEmail").when(jwtTokenProvider).getUserPk("token");

        // when
        MessageResponse result = playCountService.findMostListenedTrackByFriend("token");

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
        assertThat(((List<TrackPlayCountResponse>) result.getData())).size().isEqualTo(1);
        assertThat(((List<TrackPlayCountResponse>) result.getData()).get(0).getPlayCount()).isEqualTo(100);
    }

}
