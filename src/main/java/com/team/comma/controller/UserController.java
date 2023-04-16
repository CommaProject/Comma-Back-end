package com.team.comma.controller;

import com.team.comma.domain.User;
import com.team.comma.dto.LoginRequest;
import com.team.comma.dto.MessageResponse;
import com.team.comma.dto.RegisterRequest;
import com.team.comma.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;


@RestController
@RequiredArgsConstructor
public class UserController {

	final private UserService userService;
	
	@PostMapping("/login")
	public ResponseEntity<MessageResponse> loginUser(@RequestBody LoginRequest login) throws AccountException {
		return ResponseEntity.ok().body(userService.login(login));
	}
	
	@PostMapping("/register")
	public ResponseEntity<MessageResponse> registerUser(@RequestBody RegisterRequest register) throws AccountException {
		return ResponseEntity.ok().body(userService.register(register));
	}

	@GetMapping("/user/privacy")
	public ResponseEntity<User> getUserInfoByEmail(@CookieValue("accessToken") String accessToken) throws AccountException {
		return ResponseEntity.ok().body(userService.getUserByCookie(accessToken));
	}

}
