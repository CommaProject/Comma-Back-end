package com.team.comma.util.jwt.service;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.search.exception.TokenExpirationException;
import com.team.comma.util.jwt.exception.TokenForgeryException;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import com.team.comma.util.security.domain.RefreshToken;
import com.team.comma.util.security.domain.Token;
import com.team.comma.util.security.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

import static com.team.comma.common.constant.ResponseCodeEnum.ACCESS_TOKEN_CREATE;
import static com.team.comma.util.jwt.support.CreationCookie.deleteRefreshTokenCookie;
import static org.apache.http.cookie.SM.SET_COOKIE;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void login(Token tokenEntity) {
        RefreshToken refreshToken = RefreshToken.builder().keyEmail(tokenEntity.getKey())
            .token(tokenEntity.getRefreshToken()).build();
        String loginUserEmail = refreshToken.getKeyEmail();

        RefreshToken token = refreshTokenRepository.existsByKeyEmail(loginUserEmail);
        if (token != null) {
            refreshTokenRepository.deleteByKeyEmail(loginUserEmail);
        }
        refreshTokenRepository.save(refreshToken);

    }

    public Optional<RefreshToken> getRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken);
    }

    public ResponseEntity validateRefreshToken(HttpServletResponse response , String refreshToken) {
        try {
            RefreshToken refreshToken1 = getRefreshToken(refreshToken).get();
            String createdAccessToken = jwtTokenProvider.validateRefreshToken(refreshToken1);

            return createRefreshJson(createdAccessToken);
        } catch (NoSuchElementException e) {
            deleteRefreshTokenCookie(response);

            throw new TokenForgeryException("변조되거나, 알 수 없는 RefreshToken 입니다.");
        }
    }

    public ResponseEntity createRefreshJson(String createdAccessToken) {
        if (createdAccessToken == null) {
            throw new TokenExpirationException("Refresh 토큰이 만료되었습니다. 로그인이 필요합니다.");
        }

        ResponseCookie cookie = ResponseCookie.from("accessToken", createdAccessToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(30 * 60 * 1000)
            .domain("localhost")
            .build();

        return ResponseEntity.status(HttpStatus.OK).header(SET_COOKIE, cookie.toString())
            .body(MessageResponse.of(ACCESS_TOKEN_CREATE));
    }
}
