package com.team.comma.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team.comma.dto.MessageResponse;
import com.team.comma.service.JwtService;

import lombok.RequiredArgsConstructor;

import static com.team.comma.constant.ResponseCode.AUTHORIZATION_ERROR;

@RestController
@RequiredArgsConstructor
public class SecurityController {

	final private JwtService jwtService;

	@GetMapping(value = "/authentication/denied")
	public ResponseEntity<MessageResponse> informAuthenticationDenied(@CookieValue(name = "refreshToken" , required = false) String authorization) {
		if(authorization == null) {
			MessageResponse message = MessageResponse.builder()
					.code(AUTHORIZATION_ERROR)
					.message("인증되지 않은 사용자입니다.")
					.build();

			return new ResponseEntity(message , HttpStatus.FORBIDDEN);
		}

		return ResponseEntity.ok().body(jwtService.validateRefreshToken(authorization));
	}

	@GetMapping(value = "/authorization/denied")
	public ResponseEntity<MessageResponse> informAuthorizationDenied() {
		MessageResponse message = MessageResponse.builder()
				.code(AUTHORIZATION_ERROR)
				.message("인가되지 않은 사용자입니다.")
				.build();

		return new ResponseEntity(message , HttpStatus.FORBIDDEN);
	}

	@GetMapping(value = "/logout/message")
	public ResponseEntity<MessageResponse> logoutMessage() {
		MessageResponse message = MessageResponse.builder()
				.code(1)
				.message("로그아웃이 성공적으로 되었습니다.")
				.build();

		return ResponseEntity.ok().body(message);
	}

}
