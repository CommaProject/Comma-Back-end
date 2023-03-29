package com.team.comma.controller;

import javax.security.auth.login.AccountException;

import com.fasterxml.jackson.databind.JsonNode;
import com.team.comma.dto.oauth.SignInResponse;
import com.team.comma.dto.oauth.SignUpRequest;
import com.team.comma.dto.oauth.TokenRequest;
import com.team.comma.service.oauth.AuthService;
import com.team.comma.service.oauth.OAuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.team.comma.dto.MessageDTO;
import com.team.comma.dto.RequestUserDTO;
import com.team.comma.dto.TokenDTO;
import com.team.comma.service.UserService;

import jakarta.servlet.http.HttpServletResponse;


@RestController
public class UserController {

	@Autowired UserService userService;

	@Autowired AuthService authService;

	@Autowired OAuthUserService oAuthUserService;

	@RequestMapping(value = "/login" , method = RequestMethod.POST)
	public TokenDTO login(@RequestBody RequestUserDTO userDTO , HttpServletResponse response) throws AccountException {
		return userService.login(userDTO , response);
	}
	
	@RequestMapping(value = "/register" , method = RequestMethod.POST)
	public MessageDTO register(@RequestBody RequestUserDTO userDTO) throws AccountException {
		return userService.register(userDTO);
	}

	@GetMapping("/login/oauth2/code/{registrationId}")
	public ResponseEntity<SignInResponse> redirect(
			@PathVariable("registrationId") String registrationId
			, @RequestParam("code") String code
			, @RequestParam("state") String state) {

		return ResponseEntity.ok(
				authService.redirect(
						TokenRequest.builder()
								.registrationId(registrationId)
								.code(code)
								.state(state)
								.build()));
	}

	@PostMapping("/user/refreshToken")
	public ResponseEntity<SignInResponse> refreshToken(@RequestBody TokenRequest tokenRequest){
		return ResponseEntity.ok(authService.refreshToken(tokenRequest));
	}

	@PostMapping("/register/oauth2")
	public ResponseEntity<String> registerOAuth(@RequestBody SignUpRequest signUpRequest){
		return ResponseEntity.ok(oAuthUserService.createUser(signUpRequest));
	}

}
