package com.team.comma.spotify.favorite.service;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.favorite.artist.repository.FavoriteArtistRepository;
import com.team.comma.spotify.favorite.artist.service.FavoriteArtistService;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.security.auth.login.AccountException;
import java.util.Optional;

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class FavoriteArtistServiceTest {

    @InjectMocks
    FavoriteArtistService favoriteArtistService;

    @Mock
    FavoriteArtistRepository favoriteArtistRepository;
    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("아티스트 추가 실패 _ 찾을 수 없는 사용자")
    public void addArtistFail_notFountUser() {
        // given
        doReturn("userEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.empty()).when(userRepository).findByEmail("userEmail");

        // when
        Throwable thrown = catchThrowable(() -> favoriteArtistService.addFavoriteArtist("token" , "artistName"));

        // then
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("아티스트 추가 성공")
    public void addArtistSuccess() throws AccountException {
        // given
        doReturn("userEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.of(User.builder().build())).when(userRepository).findByEmail("userEmail");

        // when
        MessageResponse result = favoriteArtistService.addFavoriteArtist("token" , "artistName");

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
    }

    @Test
    @DisplayName("아티스트 삭제 실패 _ 찾을 수 없는 사용자")
    public void deleteArtistFail_notFountUser() {
        // given
        doReturn("userEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.empty()).when(userRepository).findByEmail("userEmail");

        // when
        Throwable thrown = catchThrowable(() -> favoriteArtistService.deleteFavoriteArtist("token" , "artistName"));

        // then
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("아티스트 삭제 성공")
    public void deleteArtistSuccess() throws AccountException {
        // given
        doReturn("userEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.of(User.builder().build())).when(userRepository).findByEmail("userEmail");

        // when
        MessageResponse result = favoriteArtistService.deleteFavoriteArtist("token" , "artistName");

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
    }
}
