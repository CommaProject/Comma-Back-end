package com.team.comma.service;

import javax.security.auth.login.AccountException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.team.comma.dto.OauthRequest;
import com.team.comma.dto.TokenDTO;
import com.team.comma.oauth.CreateOAuthUser;
import com.team.comma.oauth.GetAccessToken;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OauthService {
	
	final private CreateOAuthUser createOauthUser;
	final private GetAccessToken getAccessToken;
	
	public TokenDTO loginOauthServer(OauthRequest oauthRequest) throws AccountException {
		if(oauthRequest.getType().equals("google")) {
			JsonNode json = getAccessToken.getGoogleAccessToken(oauthRequest.getCode());
			System.out.println(json.get("access_token").toString());
			
		}
		else if(oauthRequest.getType().equals("kakao")) {
			try {
				JsonNode json = getAccessToken.getKakaoAccessToken(oauthRequest.getCode());
				createOauthUser.createKakaoUser(json.get("access_token").toString());
			} catch (NullPointerException e) {
				throw new AccountException("유효하지 않은 접근입니다.");
			}
		}
		else if(oauthRequest.getType().equals("naver")) {
			JsonNode json = getAccessToken.getNaverAccessToken(oauthRequest.getCode() , oauthRequest.getState());
			System.out.println(json.get("access_token").toString());
			
		}
		return null;
	}

}
