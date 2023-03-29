package com.team.comma.dto.oauth;

import com.team.comma.enumType.AuthProvider;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SignInResponse {
    private AuthProvider authProvider;
    private KakaoUserInfo kakaoUserInfo;
    private NaverUserInfo naverUserInfo;
    private GoogleUserInfo googleUserInfo;
    private String accessToken;
    private String refreshToken;

    @Builder
    public SignInResponse(
            AuthProvider authProvider
            ,KakaoUserInfo kakaoUserInfo
            ,NaverUserInfo naverUserInfo
            ,GoogleUserInfo googleUserInfo
            ,String accessToken
            ,String refreshToken
    ){
        this.authProvider = authProvider;
        this.kakaoUserInfo = kakaoUserInfo;
        this.naverUserInfo = naverUserInfo;
        this.googleUserInfo = googleUserInfo;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}