package com.team.comma.global.jwt.service;

import com.team.comma.spotify.exception.TokenExpirationException;
import com.team.comma.global.jwt.exception.TokenForgeryException;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import com.team.comma.global.security.domain.RefreshToken;
import com.team.comma.global.security.dto.Token;
import com.team.comma.global.security.repository.RefreshTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.apache.http.cookie.SM.SET_COOKIE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    JwtService jwtService;

    @Test
    @DisplayName("토큰 생성")
    void createToken() {
        // given
        Token token = getToken();
        RefreshToken refreshToken = getRefreshToken();
        doReturn(refreshToken).when(refreshTokenRepository)
            .existsByKeyEmail(refreshToken.getKeyEmail());
        doNothing().when(refreshTokenRepository).deleteByKeyEmail(refreshToken.getKeyEmail());
        doReturn(null).when(refreshTokenRepository).save(any(RefreshToken.class));

        // when
        Throwable thrown = catchThrowable(() -> jwtService.login(token));

        // then
        assertThat(thrown).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("새로운 AccessToken 반환")
    void createAccessToken() {
        // given
        RefreshToken refreshToken = getRefreshToken();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Optional<RefreshToken> tokens = Optional.of(refreshToken);
        doReturn(tokens).when(refreshTokenRepository).findByToken(refreshToken.getToken());
        doReturn("Token").when(jwtTokenProvider).validateRefreshToken(any(RefreshToken.class));

        // when
        ResponseEntity result = jwtService.validateRefreshToken(response , refreshToken.getToken());

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getHeaders().get(SET_COOKIE).toString()).contains("accessToken=Token");
    }

    @Test
    @DisplayName("새로운 AccessToken 반환 예외 _ 만료된 RefreshToken")
    void expireToken() {
        // given
        RefreshToken refreshToken = getRefreshToken();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Optional<RefreshToken> tokens = Optional.of(refreshToken);
        doReturn(tokens).when(refreshTokenRepository).findByToken(refreshToken.getToken());
        doReturn(null).when(jwtTokenProvider).validateRefreshToken(any(RefreshToken.class));

        // when
        Throwable thrown = catchThrowable(
            () -> jwtService.validateRefreshToken(response , refreshToken.getToken()));

        // then
        assertThat(thrown).isInstanceOf(TokenExpirationException.class)
            .hasMessage("Refresh 토큰이 만료되었습니다. 로그인이 필요합니다.");
    }

    // testssss
    @Test
    @DisplayName("새로운 AccessToken 반환 예외 _ 변조된 RefreshToken")
    void falsifyToken() {
        // given
        RefreshToken refreshToken = getRefreshToken();
        MockHttpServletResponse response = new MockHttpServletResponse();
        doThrow(NoSuchElementException.class).when(refreshTokenRepository)
            .findByToken(refreshToken.getToken());

        // when
        Throwable thrown = catchThrowable(
            () -> jwtService.validateRefreshToken(response , refreshToken.getToken()));

        // then
        assertThat(thrown).isInstanceOf(TokenForgeryException.class)
            .hasMessage("변조되거나, 알 수 없는 RefreshToken 입니다.");

    }

    public RefreshToken getRefreshToken() {
        return RefreshToken.builder().keyEmail("keyEmail").token("refreshToken").build();
    }

    public Token getToken() {
        return Token.builder().key("keyEmail").refreshToken("refreshToken").build();
    }

}
