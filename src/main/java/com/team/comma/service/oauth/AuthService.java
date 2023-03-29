package com.team.comma.service.oauth;

import com.team.comma.dto.oauth.SignInResponse;
import com.team.comma.dto.oauth.TokenRequest;
import com.team.comma.dto.oauth.TokenResponse;
import com.team.comma.enumType.AuthProvider;
import com.team.comma.exception.BadRequestException;
import com.team.comma.repository.OAuthRepository;
import com.team.comma.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final KakaoRequestService kakaoRequestService;
    private final NaverRequestService naverRequestService;
    private final GoogleRequestService googleRequestService;
    private final SecurityUtil securityUtil;
    private final OAuthRepository oAuthRepository;


    public SignInResponse redirect(TokenRequest tokenRequest){
        if(AuthProvider.KAKAO.getAuthProvider().equals(tokenRequest.getRegistrationId())){
            return kakaoRequestService.redirect(tokenRequest);
        } else if(AuthProvider.NAVER.getAuthProvider().equals(tokenRequest.getRegistrationId())){
            return naverRequestService.redirect(tokenRequest);
        } else if(AuthProvider.GOOGLE.getAuthProvider().equals(tokenRequest.getRegistrationId())) {
            return googleRequestService.redirect(tokenRequest);
        }

        throw new BadRequestException("not supported oauth provider");
    }

    public SignInResponse refreshToken(TokenRequest tokenRequest){
        String userId = (String) securityUtil.get(tokenRequest.getRefreshToken()).get("userId");
        String provider = (String) securityUtil.get(tokenRequest.getRefreshToken()).get("provider");
        String oldRefreshToken = (String) securityUtil.get(tokenRequest.getRefreshToken()).get("refreshToken");

        if(!oAuthRepository.existsByIdAndAuthProvider(userId, AuthProvider.findByCode(provider))){
            throw new BadRequestException("CANNOT_FOUND_USER");
        }

        TokenResponse tokenResponse = null;
        if(AuthProvider.KAKAO.getAuthProvider().equals(provider.toLowerCase())){
            tokenResponse = kakaoRequestService.getRefreshToken(provider, oldRefreshToken);
        } else if(AuthProvider.NAVER.getAuthProvider().equals(provider.toLowerCase())){
            tokenResponse = naverRequestService.getRefreshToken(provider, oldRefreshToken);
        } else if(AuthProvider.GOOGLE.getAuthProvider().equals(provider.toLowerCase())){
            tokenResponse = googleRequestService.getRefreshToken(provider, oldRefreshToken);
        }

        String accessToken = securityUtil.createAccessToken(
                userId, AuthProvider.findByCode(provider.toLowerCase()), tokenResponse.getAccessToken());

        return SignInResponse.builder()
                .authProvider(AuthProvider.findByCode(provider.toLowerCase()))
                .kakaoUserInfo(null)
                .accessToken(accessToken)
                .refreshToken(null)
                .build();
    }
}