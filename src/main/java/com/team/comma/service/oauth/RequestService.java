package com.team.comma.service.oauth;

import com.team.comma.dto.oauth.SignInResponse;
import com.team.comma.dto.oauth.TokenRequest;
import com.team.comma.dto.oauth.TokenResponse;

public interface RequestService<T> {
    SignInResponse redirect(TokenRequest tokenRequest);
    TokenResponse getToken(TokenRequest tokenRequest);
    T getUserInfo(String accessToken);
    TokenResponse getRefreshToken(String provider, String refreshToken);
}