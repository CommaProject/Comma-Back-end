package com.team.comma.service;

import com.team.comma.constant.ResponseCode;
import javax.security.auth.login.AccountException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.team.comma.dto.MessageResponse;
import com.team.comma.dto.OAuthRequest;
import com.team.comma.util.oauth.RegisterationOAuthUser;
import com.team.comma.util.oauth.IssuanceAccessToken;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthService {
	
	final private RegisterationOAuthUser createOAuthUser;
	final private IssuanceAccessToken getAccessToken;
	
	public ResponseEntity<MessageResponse> loginOAuthServer(OAuthRequest oauthRequest) throws AccountException {
		if(oauthRequest.getType().equals("google")) {
			try {
				JsonNode json = getAccessToken.getGoogleAccessToken(oauthRequest.getCode());
				MessageResponse result = createOAuthUser.createGoogleUser(json.get("access_token").toString());
				return ResponseEntity.ok(result);
			} catch (NullPointerException e) {
				throw new AccountException("유효하지 않은 접근입니다.");
			}
		}
		else if(oauthRequest.getType().equals("kakao")) {
			try {
				JsonNode json = getAccessToken.getKakaoAccessToken(oauthRequest.getCode());
				MessageResponse result = createOAuthUser.createKakaoUser(json.get("access_token").toString());
				return ResponseEntity.ok(result);
			} catch (NullPointerException e) {
				throw new AccountException("유효하지 않은 접근입니다.");
			}
		}
		else if(oauthRequest.getType().equals("naver")) {
			try {
				JsonNode json = getAccessToken.getNaverAccessToken(oauthRequest.getCode() , oauthRequest.getState());
				MessageResponse result = createOAuthUser.createNaverUser(json.get("access_token").toString());
				return ResponseEntity.ok(result);
			} catch (NullPointerException e) {
				throw new AccountException("유효하지 않은 접근입니다.");
			}
		}
		
		MessageResponse<Object> message = MessageResponse.builder()
			.code(ResponseCode.SIMPLE_REQUEST_FAILURE)
			.message("잘못된 소셜서버입니다.")
			.build();

		return ResponseEntity.ok(message);
	}

}
