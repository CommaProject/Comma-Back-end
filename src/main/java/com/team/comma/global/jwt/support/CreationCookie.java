package com.team.comma.global.jwt.support;

import com.team.comma.global.security.domain.Token;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class CreationCookie {

    public static ResponseCookie createResponseAccessToken(String cookieName , long age) {
        return ResponseCookie.from("accessToken", cookieName)
                .httpOnly(true)
                //.secure(true)
                .path("/")
                .domain("comma-project.p-e.kr")
                //.sameSite(Cookie.SameSite.NONE.name())
                .maxAge(age)
                .build();
    }

    public static ResponseCookie createResponseRefreshToken(String cookieName , long age) {
        return ResponseCookie.from("refreshToken", cookieName)
                .httpOnly(true)
                //.secure(true)
                .path("/")
                .domain("comma-project.p-e.kr")
                //.sameSite(Cookie.SameSite.NONE.name())
                .maxAge(age)
                .build();
    }

    public static void setCookieFromJwt(HttpServletResponse response , Token token) {
        response.addHeader("Set-Cookie" , createResponseAccessToken(token.getAccessToken() , 30 * 60 * 1000L).toString());
        response.addHeader("Set-Cookie" , createResponseRefreshToken(token.getRefreshToken() , 14 * 24 * 60 * 60 * 1000L).toString());
    }

    public static void deleteCookie(HttpServletResponse response) {
        deleteAccessTokenCookie(response);
        deleteRefreshTokenCookie(response);
    }

    public static void deleteAccessTokenCookie(HttpServletResponse response) {
        response.addHeader("Set-Cookie" , createResponseAccessToken("accessToken" , 0).toString());
    }

    public static void deleteRefreshTokenCookie(HttpServletResponse response) {
        response.addHeader("Set-Cookie" , createResponseRefreshToken("refreshToken" , 0).toString());
    }

}
