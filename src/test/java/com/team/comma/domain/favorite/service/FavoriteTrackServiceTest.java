package com.team.comma.domain.favorite.service;

import com.team.comma.domain.favorite.repository.FavoriteTrackRepository;
import com.team.comma.domain.track.domain.Track;
import com.team.comma.domain.track.dto.TrackRequest;
import com.team.comma.domain.track.service.TrackService;
import com.team.comma.domain.user.constant.UserRole;
import com.team.comma.domain.user.domain.User;
import com.team.comma.domain.user.repository.user.UserRepository;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.security.auth.login.AccountException;
import java.util.Optional;

import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class FavoriteTrackServiceTest {

    @InjectMocks
    FavoriteTrackService favoriteTrackService;

    @Mock
    TrackService trackService;

    @Mock
    FavoriteTrackRepository favoriteTrackRepository;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("좋아요 트랙 설정 실패 _ 사용자 정보 찾기 실패")
    void addFavoriteTrackFail_notFountUser() {
        // given
        doReturn("userEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.empty()).when(userRepository).findByEmail(any(String.class));

        // when
        Throwable throwable = catchThrowable(() -> favoriteTrackService.createFavoriteTrack("token", null));

        // then
        assertThat(throwable.getMessage()).isEqualTo("사용자 정보를 찾을 수 없습니다.");
    }


    @Test
    @DisplayName("좋아요 트랙 저장")
    void createFavoriteTrack() throws AccountException {
        // given
        TrackRequest trackRequest = TrackRequest.builder()
                .trackTitle("title")
                .trackArtistList(null)
                .spotifyTrackId("id")
                .spotifyTrackHref("href")
                .albumImageUrl("img")
                .durationTimeMs(0)
                .build();

        doReturn("userEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.of(buildUser())).when(userRepository).findByEmail(any(String.class));
        doReturn(buildTrack("title" , "spotifyAPI")).when(trackService).findTrackOrElseSave(trackRequest.getSpotifyTrackId());

        // when
        MessageResponse result = favoriteTrackService.createFavoriteTrack("token", trackRequest);

        // then
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
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

    private User buildUser() {
        return User.builder()
                .id(0L)
                .email("email")
                .password("password")
                .role(UserRole.USER)
                .build();
    }

}
