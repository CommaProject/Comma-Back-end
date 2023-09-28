package com.team.comma.domain.favorite.track.service;

import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.artist.dto.ArtistResponse;
import com.team.comma.domain.favorite.track.domain.FavoriteTrack;
import com.team.comma.domain.favorite.track.dto.FavoriteTrackRequest;
import com.team.comma.domain.favorite.track.dto.FavoriteTrackResponse;
import com.team.comma.domain.favorite.track.repository.FavoriteTrackRepository;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.artist.dto.TrackArtistResponse;
import com.team.comma.domain.track.track.dto.TrackResponse;
import com.team.comma.domain.track.track.service.TrackService;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.exception.UserException;
import com.team.comma.domain.user.user.service.UserService;
import com.team.comma.global.message.MessageResponse;
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

import static com.team.comma.global.constant.ResponseCodeEnum.NOT_FOUNT_USER;
import static com.team.comma.global.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

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
    UserService userService;

    @Test
    @DisplayName("좋아요 트랙 설정 실패 _ 사용자 정보 찾기 실패")
    void createFavoriteTrackFail_UserNotFound() {
        // given
        doReturn("userEmail").when(jwtTokenProvider).getUserPk(anyString());
        doThrow(new UserException(NOT_FOUNT_USER)).when(userService).findUserOrThrow(anyString());

        // when
        Throwable throwable = catchThrowable(() -> favoriteTrackService.createFavoriteTrack("token", null));

        // then
        assertThat(throwable.getMessage()).isEqualTo("사용자 정보를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("좋아요 트랙 저장")
    void createFavoriteTrack() throws AccountException {
        // given
        FavoriteTrackRequest favoriteTrackRequest = FavoriteTrackRequest.buildFavoriteTrackRequest();

        User user = User.createUser();
        Track track = Track.buildTrack();

        doReturn("userEmail").when(jwtTokenProvider).getUserPk(anyString());
        doReturn(user).when(userService).findUserOrThrow(anyString());
        doReturn(track).when(trackService).findTrackOrSave(anyString());

        // when
        MessageResponse result = favoriteTrackService.createFavoriteTrack("token", favoriteTrackRequest);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
    }

    @Test
    @DisplayName("트랙 좋아요 리스트 조회")
    void findAllByUser() {
        // given
        User user = User.createUser();

        Artist artist = Artist.buildArtist();
        Track track = Track.buildTrack();
        FavoriteTrack favoriteTrack = FavoriteTrack.buildFavoriteTrack(user, track);

        ArtistResponse artistResponse = ArtistResponse.of(artist);
        TrackResponse trackResponse = TrackResponse.of(track);
        TrackArtistResponse trackArtistResponse = TrackArtistResponse.of(trackResponse , artistResponse);

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
        User user = User.createUser();
        Artist artist = Artist.buildArtist();
        Track track = Track.buildTrack();
        FavoriteTrack favoriteTrack = FavoriteTrack.buildFavoriteTrack(user, track);

        ArtistResponse artistResponse = ArtistResponse.of(artist);
        TrackResponse trackResponse = TrackResponse.of(track);
        TrackArtistResponse trackArtistResponse = TrackArtistResponse.of(trackResponse , artistResponse);

        FavoriteTrackResponse favoriteTrackResponse = FavoriteTrackResponse.of(favoriteTrack, List.of(trackArtistResponse));

        doReturn(favoriteTrack.getUser().getEmail()).when(jwtTokenProvider).getUserPk(accessToken);
        doReturn(favoriteTrack.getUser()).when(userService).findUserOrThrow(favoriteTrack.getUser().getEmail());
        doReturn(List.of(favoriteTrackResponse)).when(favoriteTrackRepository).findAllFavoriteTrackByUser(favoriteTrack.getUser());

        // when
        MessageResponse result = favoriteTrackService.findAllFavoriteTrack(accessToken);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());

    }

    @Test
    void deleteFavoriteTrack() {
        // given
        User user = User.createUser();
        Track track = Track.buildTrack();
        FavoriteTrack favoriteTrack = FavoriteTrack.buildFavoriteTrack(user, track);

        doReturn(favoriteTrack.getUser().getEmail()).when(jwtTokenProvider).getUserPk(anyString());
        doReturn(favoriteTrack.getUser()).when(userService).findUserOrThrow(anyString());
        doReturn(Optional.of(favoriteTrack)).when(favoriteTrackRepository).findById(favoriteTrack.getId());

        // when
        MessageResponse result = favoriteTrackService.deleteFavoriteTrack("token", favoriteTrack.getId());

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());

        List<FavoriteTrackResponse> favoriteTrackResponses = favoriteTrackRepository.findAllFavoriteTrackByUser(favoriteTrack.getUser());
        assertThat(favoriteTrackResponses.size()).isEqualTo(0);
    }

}
