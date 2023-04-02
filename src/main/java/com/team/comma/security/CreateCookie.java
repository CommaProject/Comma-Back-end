package com.team.comma.security;

import jakarta.servlet.http.Cookie;

public class CreateCookie {

	public static Cookie createRefreshToken(String refreshToken) {
		Cookie cookie = new Cookie("refreshToken", "sex");
		cookie.setDomain("localhost");
		cookie.setPath("/");
		// 14주간 저장
		cookie.setMaxAge(14 * 24 * 60 * 60 * 1000);
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		
		return cookie;
	}
	
}
