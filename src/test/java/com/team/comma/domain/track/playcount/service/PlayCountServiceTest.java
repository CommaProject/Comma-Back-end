package com.team.comma.domain.track.playcount.service;

import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.track.artist.domain.TrackArtist;
import com.team.comma.domain.track.playcount.domain.TrackPlayCount;
import com.team.comma.domain.track.playcount.dto.TrackPlayCountResponse;
import com.team.comma.domain.track.playcount.repository.TrackPlayCountRepository;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.track.dto.TrackResponse;
import com.team.comma.domain.track.track.exception.TrackException;
import com.team.comma.global.common.dto.MessageResponse;
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
import java.util.Optional;

import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
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
    @DisplayName("TrackPlayCount 탐색 실패")
    void searchTrackPlayCountFail() {
        // given
        doThrow(new TrackException("트랙을 찾을 수 없습니다.")).when(trackPlayCountRepository).findTrackPlayCountByUserEmail(any(String.class), any(String.class));
        doReturn("userEmail").when(jwtTokenProvider).getUserPk("token");
        // when
        Throwable thrown = catchThrowable(() -> playCountService.modifyPlayCount("token", "trackId"));

        // then
        assertThat(thrown.getMessage()).isEqualTo("트랙을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("TrackPlayCount 탐색 성공")
    void searchTrackPlayCount() throws AccountException {
        // given
        doReturn(Optional.of(buildTrackPlayCount(buildTrack()))).when(trackPlayCountRepository).findTrackPlayCountByUserEmail(any(String.class), any(String.class));
        doReturn("userEmail").when(jwtTokenProvider).getUserPk("token");
        // when
        MessageResponse result = playCountService.modifyPlayCount("token", "trackId");

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    @Test
    @DisplayName("내가 가장 많이 들은 곡")
    void findMostListenedTrack() {
        // given
        List<TrackPlayCountResponse> trackPlayCounts = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            trackPlayCounts.add(buildTrackPlayCountResponse(buildTrackResponse()));
        }
        doReturn(trackPlayCounts).when(trackPlayCountRepository).findTrackPlayCountByMostListenedTrack(any(String.class));
        doReturn("userEmail").when(jwtTokenProvider).getUserPk("token");

        // when
        MessageResponse result = playCountService.findMostListenedTrack("token");

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
        assertThat(((List) result.getData()).size()).isEqualTo(5);
    }

    @Test
    @DisplayName("친구가 가장 많이 들은 곡")
    void findMostListenedTrackByFriend() {
        // given
        List<TrackPlayCountResponse> trackPlayCounts = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            trackPlayCounts.add(buildTrackPlayCountResponse(buildTrackResponse()));
        }
        doReturn(trackPlayCounts).when(trackPlayCountRepository).findTrackPlayCountByFriend(any(String.class));
        doReturn("userEmail").when(jwtTokenProvider).getUserPk("token");

        // when
        MessageResponse result = playCountService.findMostListenedTrackByFriend("token");

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
        assertThat(((List) result.getData()).size()).isEqualTo(5);
    }

    public TrackPlayCountResponse buildTrackPlayCountResponse(TrackResponse trackResponse) {
        return TrackPlayCountResponse.builder()
                .playCount(0)
                .track(trackResponse)
                .build();
    }

    public TrackResponse buildTrackResponse() {
        return TrackResponse.builder()
                .albumImageUrl("albumImageURL")
                .durationTimeMs(30)
                .recommendCount(1L)
                .trackTitle("title")
                .spotifyTrackHref("href")
                .spotifyTrackId("id")
                .id(30L)
                .build();
    }

    public TrackPlayCount buildTrackPlayCount(Track track) {
        return TrackPlayCount.builder()
                .playCount(0)
                .track(track)
                .build();
    }

    private Track buildTrack() {
        return Track.builder()
                .id(1L)
                .trackTitle("title")
                .recommendCount(0L)
                .albumImageUrl("url")
                .spotifyTrackHref("spotifyTrackHref")
                .spotifyTrackId("spotifyId")
                .build();
    }

}
