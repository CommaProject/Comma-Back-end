package com.team.comma.global.security.controller;

import com.team.comma.global.constant.ResponseCodeEnum;
import com.team.comma.global.message.MessageResponse;
import com.team.comma.global.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.team.comma.global.constant.ResponseCodeEnum.AUTHENTICATION_ERROR;
import static com.team.comma.global.constant.ResponseCodeEnum.AUTHORIZATION_ERROR;

@RestController
@RequiredArgsConstructor
public class SecurityController {

    private final JwtService jwtService;

    @GetMapping(value = "/authentication/denied")
    public ResponseEntity<MessageResponse> informAuthenticationDenied(
            @CookieValue(name = "refreshToken", required = false) String authorization , HttpServletResponse response) {
        if (authorization == null) {
            MessageResponse message = MessageResponse.of(AUTHENTICATION_ERROR, "인증되지 않은 사용자입니다.");

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
        }

        return jwtService.validateRefreshToken(response , authorization);
    }

    @GetMapping(value = "/authorization/denied")
    public ResponseEntity<MessageResponse> informAuthorizationDenied() {
        MessageResponse message = MessageResponse.of(AUTHORIZATION_ERROR , "인가되지 않은 사용자입니다.");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
    }

    @GetMapping(value = "/logout/message")
    public ResponseEntity<MessageResponse> logoutMessage() {
        MessageResponse message = MessageResponse.of(ResponseCodeEnum.LOGOUT_SUCCESS);

        return ResponseEntity.ok().body(message);
    }

}
