package com.team.comma.domain.favorite.artist.service;

import com.team.comma.domain.favorite.artist.dto.FavoriteArtistResponse;
import com.team.comma.domain.user.user.constant.UserRole;
import com.team.comma.domain.user.user.constant.UserType;
import com.team.comma.global.common.constant.ResponseCodeEnum;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.domain.favorite.artist.domain.FavoriteArtist;
import com.team.comma.domain.favorite.artist.exception.FavoriteArtistException;
import com.team.comma.domain.favorite.artist.repository.FavoriteArtistRepository;
import com.team.comma.domain.favorite.artist.service.FavoriteArtistService;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.domain.favorite.artist.domain.FavoriteArtist;
import com.team.comma.domain.favorite.artist.exception.FavoriteArtistException;
import com.team.comma.domain.favorite.artist.repository.FavoriteArtistRepository;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.security.auth.login.AccountException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
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
        Throwable thrown = catchThrowable(() -> favoriteArtistService.createFavoriteArtist("token" , "artistName"));

        // then
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("아티스트 추가 실패 _ 이미 추가된 아티스트")
    public void addArtistFail_alreadyAddedArtist() {
        // given
        doReturn("userEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.of(User.builder().build())).when(userRepository).findByEmail("userEmail");
        doReturn(Optional.of(FavoriteArtist.builder().build())).when(favoriteArtistRepository).findFavoriteArtistByUser(any(User.class) , any(String.class));

        // when
        Throwable thrown = catchThrowable(() -> favoriteArtistService.createFavoriteArtist("token" , "artistName"));

        // then
        assertThat(thrown).isInstanceOf(FavoriteArtistException.class).hasMessage("이미 추가된 관심 아티스트입니다.");
    }

    @Test
    @DisplayName("아티스트 추가 성공")
    public void addArtistSuccess() throws AccountException {
        // given
        doReturn("userEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.of(User.builder().build())).when(userRepository).findByEmail("userEmail");
        doReturn(Optional.empty()).when(favoriteArtistRepository).findFavoriteArtistByUser(any(User.class) , any(String.class));

        // when
        MessageResponse result = favoriteArtistService.createFavoriteArtist("token" , "artistName");

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

    @ParameterizedTest
    @MethodSource("isAddedArtist")
    @DisplayName("아티스트 추가 여부 ( 참 , 거짓 ) ")
    public void isAddedArtist_false(Optional<FavoriteArtist> optional , boolean isAdded) throws AccountException {
        // given
        doReturn("userEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.of(User.builder().build())).when(userRepository).findByEmail("userEmail");
        doReturn(optional).when(favoriteArtistRepository).findFavoriteArtistByUser(any(User.class) , any(String.class));

        // when
        MessageResponse result = favoriteArtistService.isFavoriteArtist("token" , "artistName");

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getData()).isEqualTo(isAdded);
    }

    @Test
    @DisplayName("아티스트 좋아요 리스트 조회")
    public void findAllByUser() {
        // given
        User user = buildUser();
        FavoriteArtist favoriteArtist = FavoriteArtist.buildFavoriteArtist(user, "artistName");
        doReturn(List.of(favoriteArtist)).when(favoriteArtistRepository).findAllFavoriteArtistByUser(user);

        // when
        List<FavoriteArtistResponse> result = favoriteArtistService.findAllFavoriteArtistByUser(user);

        // then
        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    @DisplayName("아티스트 좋아요 Response 리스트 조회")
    public void findALlFavoriteArtist() throws AccountException {
        // given
        User user = buildUser();
        FavoriteArtist favoriteArtist = FavoriteArtist.buildFavoriteArtist(user, "artistName");
        FavoriteArtistResponse favoriteArtistResponse = FavoriteArtistResponse.of(favoriteArtist);

        doReturn(user.getEmail()).when(jwtTokenProvider).getUserPk("accessToken");
        doReturn(Optional.of(user)).when(userRepository).findByEmail(user.getEmail());
        doReturn(List.of(favoriteArtistResponse)).when(favoriteArtistRepository).findAllFavoriteArtistByUser(user);

        // when
        MessageResponse result = favoriteArtistService.findALlFavoriteArtist("accessToken");

        // then
        assertThat(result.getCode()).isEqualTo(ResponseCodeEnum.REQUEST_SUCCESS.getCode());

    }

    private User buildUser() {
        return User.builder()
                .email("userEmail")
                .password("userPassword")
                .type(UserType.GENERAL_USER)
                .role(UserRole.USER)
                .build();
    }

    private static Stream<Arguments> isAddedArtist() {
        return Stream.of(
                Arguments.of(Optional.empty(), false),
                Arguments.of(Optional.of(FavoriteArtist.builder().build()), true)
        );
    }

}
