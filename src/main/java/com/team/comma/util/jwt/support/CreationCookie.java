package com.team.comma.util.jwt.support;

import com.team.comma.util.security.domain.Token;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.ResponseCookie;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class CreationCookie {

    public static ResponseCookie createResponseAccessToken(String cookieName) {
        return ResponseCookie.from("accessToken", cookieName)
                //.httpOnly(true)
                //.secure(true)
                .path("/")
                .domain("comma-project.p-e.kr")
                //.sameSite(Cookie.SameSite.NONE.name())
                .maxAge(30 * 60 * 1000L)
                .build();
    }

    public static ResponseCookie createResponseRefreshToken(String cookieName) {
        return ResponseCookie.from("refreshToken", cookieName)
                //.httpOnly(true)
                //.secure(true)
                .path("/")
                .domain("comma-project.p-e.kr")
                //.sameSite(Cookie.SameSite.NONE.name())
                .maxAge(14 * 24 * 60 * 60 * 1000L)
                .build();
    }

    public static void setCookieFromJwt(HttpServletResponse response , Token token) {
        response.addHeader("Set-Cookie" , createResponseAccessToken(token.getAccessToken()).toString());
        response.addHeader("Set-Cookie" , createResponseRefreshToken(token.getRefreshToken()).toString());
    }

}
