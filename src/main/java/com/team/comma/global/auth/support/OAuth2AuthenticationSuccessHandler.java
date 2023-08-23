package com.team.comma.global.auth.support;

import com.team.comma.global.jwt.support.CreationCookie;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import com.team.comma.global.security.dto.Token;
import com.team.comma.domain.user.constant.UserRole;
import com.team.comma.domain.user.dto.user.UserSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Data
@EqualsAndHashCode(callSuper = false)
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    final private JwtTokenProvider jwtTokenProvider;
    final private HttpSession httpSession;

    @Value("${client.url}")
    private String clientUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        UserSession user = (UserSession) httpSession.getAttribute("user");

        if (user == null) {
            getRedirectStrategy().sendRedirect(request, response,
                createRedirectUrl(clientUrl + "/oauth2/disallowance"));
            return;
        }

        Token token = jwtTokenProvider.createAccessToken(user.getEmail(), UserRole.USER);

        response.addHeader("Set-Cookie" , CreationCookie.createResponseAccessToken(token.getAccessToken() , 2 * 60 * 60 * 1000L).toString());
        response.addHeader("Set-Cookie" , CreationCookie.createResponseRefreshToken(token.getRefreshToken() , 14 * 24 * 60 * 60 * 1000L).toString());

        httpSession.removeAttribute("user");

        getRedirectStrategy().sendRedirect(request, response, createRedirectUrl(clientUrl));
    }

    public String createRedirectUrl(String url) {
        return UriComponentsBuilder.fromUriString(url).build().toUriString();
    }

}

