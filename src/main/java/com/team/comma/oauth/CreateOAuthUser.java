package com.team.comma.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.security.auth.login.AccountException;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.team.comma.dto.TokenDTO;
import com.team.comma.entity.Token;
import com.team.comma.entity.UserEntity;
import com.team.comma.repository.UserRepository;
import com.team.comma.security.CreateCookie;
import com.team.comma.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateOAuthUser {
	
	final private UserService userService;
	
	public TokenDTO createKakaoUser(String token) throws AccountException {
		String reqURL = "https://kapi.kakao.com/v2/user/me"; // access_token을 이용하여 사용자 정보 조회
		try {
			URL url = new URL(reqURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Authorization", "Bearer " + token); // 전송할 header 작성, access_token전송

			// 결과 코드가 200이라면 성공
			int responseCode = conn.getResponseCode();
			System.out.println("responseCode : " + responseCode);

			// 요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			String result = "";

			while ((line = br.readLine()) != null) {
				result += line;
			}
			System.out.println("response body : " + result);

			// Gson 라이브러리로 JSON파싱
			JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();
			String email = jsonObject.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
			
			// userService.loginOauth(email);
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
