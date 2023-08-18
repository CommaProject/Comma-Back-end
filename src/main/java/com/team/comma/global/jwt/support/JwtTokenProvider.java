package com.team.comma.global.jwt.support;

import com.team.comma.global.jwt.exception.RequireRefreshToken;
import com.team.comma.global.jwt.exception.TokenForgeryException;
import com.team.comma.global.security.domain.RefreshToken;
import com.team.comma.global.security.domain.Token;
import com.team.comma.domain.user.constant.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private String secretKey = "secretKey";
    private String refreshKey = "refreshKey";

    private final UserDetailsService userDetailsService;

    private long tokenValidTime = 2 * 60 * 60 * 1000L;
    private long refreshTokenValidTime = 14 * 24 * 60 * 60 * 1000L;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        refreshKey = Base64.getEncoder().encodeToString(refreshKey.getBytes());
    }

    public Token createAccessToken(String userPk, UserRole role) {
        Claims claims = Jwts.claims()
            .setSubject(userPk);
        claims.put("roles", role);
        Date now = new Date();

        String accessToken = Jwts.builder().setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + tokenValidTime))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();

        // RefreshToken 발급
        String refreshToken = Jwts.builder().setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
            .signWith(SignatureAlgorithm.HS256, refreshKey)
            .compact();

        return Token.builder().accessToken(accessToken).refreshToken(refreshToken).key(userPk)
            .build();
    }

    public String validateRefreshToken(RefreshToken refreshTokenObj) {
        String refreshToken = refreshTokenObj.getToken();
        try {
            // 검증
            Jws<Claims> claims = Jwts.parser().setSigningKey(refreshKey)
                .parseClaimsJws(refreshToken);

            if (!claims.getBody().getExpiration().before(new Date())) {
                return recreationAccessToken(claims.getBody().get("sub").toString(),
                    claims.getBody().get("roles"));
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public String recreationAccessToken(String userEmail, Object roles) {
        Claims claims = Jwts.claims().setSubject(userEmail);
        claims.put("roles", roles);
        Date now = new Date();

        String accessToken = Jwts.builder().setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + tokenValidTime))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();

        return accessToken;
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        if (userDetails == null) {
            throw new TokenForgeryException("알 수 없는 토큰이거나 , 변조되었습니다.");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, "",
            userDetails.getAuthorities());
    }

    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            throw new RequireRefreshToken("AccessToken이 만료되었습니다.");
        }
    }
}
