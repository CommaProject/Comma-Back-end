package com.team.comma.domain.favorite.track.service;

import com.team.comma.domain.favorite.track.domain.FavoriteTrack;
import com.team.comma.domain.favorite.track.dto.FavoriteTrackResponse;
import com.team.comma.domain.favorite.track.repository.FavoriteTrackRepository;
import com.team.comma.domain.playlist.track.dto.PlaylistTrackArtistResponse;
import com.team.comma.domain.track.artist.domain.TrackArtist;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.track.dto.TrackRequest;
import com.team.comma.domain.track.track.service.TrackService;
import com.team.comma.domain.user.user.constant.UserRole;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.security.auth.login.AccountException;
import java.util.List;
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
    void createFavoriteTrackFail_UserNotFound() {
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
        doReturn(buildTrack("title" , "spotifyAPI")).when(trackService).findTrackOrSave(trackRequest.getSpotifyTrackId());

        // when
        MessageResponse result = favoriteTrackService.createFavoriteTrack("token", trackRequest);

        // then
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    @Test
    @DisplayName("트랙 좋아요 리스트 조회")
    void findAllByUser() {
        // given
        User user = buildUser();
        Track track = buildTrack("track title", "spotify id");
        FavoriteTrack favoriteTrack = buildFavoriteTrackWithTrackAndUser(track, user);
        TrackArtist trackArtist = buildTrackArtist(track);
        PlaylistTrackArtistResponse trackArtistResponse = PlaylistTrackArtistResponse.of(trackArtist);
        FavoriteTrackResponse favoriteTrackResponse = FavoriteTrackResponse.of(favoriteTrack, List.of(trackArtistResponse));

        doReturn(List.of(favoriteTrackResponse)).when(favoriteTrackRepository).findAllFavoriteTrackByUser(user);

        // when
        List<FavoriteTrackResponse> result = favoriteTrackService.findAllFavoriteTrackByUser(user);

        // then
        assertThat(result).isEqualTo(List.of(favoriteTrackResponse));

    }

    @Test
    @DisplayName("트랙 좋아요 Response 리스트 조회")
    void findAllFavoriteTrack() throws AccountException {
        // given
        String accessToken = "accessToken";
        User user = buildUser();
        Track track = buildTrack("track title", "spotify id");
        track.addTrackArtistList("artist name");
        FavoriteTrack favoriteTrack = buildFavoriteTrackWithTrackAndUser(track, user);
        TrackArtist trackArtist = buildTrackArtist(track);
        PlaylistTrackArtistResponse trackArtistResponse = PlaylistTrackArtistResponse.of(trackArtist);
        FavoriteTrackResponse favoriteTrackResponse = FavoriteTrackResponse.of(favoriteTrack, List.of(trackArtistResponse));

        doReturn(user.getEmail()).when(jwtTokenProvider).getUserPk(accessToken);
        doReturn(Optional.of(user)).when(userRepository).findByEmail(user.getEmail());
        doReturn(List.of(favoriteTrackResponse)).when(favoriteTrackRepository).findAllFavoriteTrackByUser(user);

        // when
        MessageResponse result = favoriteTrackService.findAllFavoriteTrack(accessToken);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());

    }

    private FavoriteTrack buildFavoriteTrackWithTrackAndUser(Track track, User user){
        return FavoriteTrack.builder()
                .id(1L)
                .track(track)
                .user(user)
                .build();
    }

    private Track buildTrack(String title, String spotifyId) {
        return Track.builder()
                .id(1L)
                .trackTitle(title)
                .recommendCount(0L)
                .albumImageUrl("url")
                .spotifyTrackHref("spotifyTrackHref")
                .spotifyTrackId(spotifyId)
                .build();
    }

    public TrackArtist buildTrackArtist(Track track) {
        return TrackArtist.builder()
                .id(1L)
                .track(track)
                .artistName("artist name")
                .build();
    }

    private User buildUser() {
        return User.builder()
                .id(1L)
                .email("email")
                .password("password")
                .role(UserRole.USER)
                .build();
    }

}
