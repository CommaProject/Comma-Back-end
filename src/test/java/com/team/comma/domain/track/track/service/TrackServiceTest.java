package com.team.comma.domain.track.track.service;

import com.team.comma.global.message.MessageResponse;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.favorite.track.repository.FavoriteTrackRepository;
import com.team.comma.domain.track.track.repository.TrackRepository;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.security.auth.login.AccountException;
import java.util.ArrayList;
import java.util.List;

import static com.team.comma.global.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class TrackServiceTest {

    @InjectMocks
    TrackService trackService;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    FavoriteTrackRepository favoriteTrackRepository;

    @Mock
    TrackRepository trackRepository;

    @Test
    @DisplayName("가장 많은 추천 곡 가져오기")
    void findTrackMostRecommended() throws AccountException {
        // given
        List<Track> tracks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            tracks.add(buildTrack("title", "trackId"));
        }
        doReturn(tracks).when(trackRepository).findTrackMostRecommended();

        // when
        MessageResponse result = trackService.findTrackByMostFavorite();

        // then
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
        assertThat(((List) result.getData()).size()).isEqualTo(5);
    }

    private Track buildTrack(String title, String spotifyId) {
        return Track.builder()
                .trackTitle(title)
                .recommendCount(0L)
                .albumImageUrl("url")
                .spotifyTrackHref("spotifyTrackHref")
                .spotifyTrackId(spotifyId)
                .build();
    }

}
