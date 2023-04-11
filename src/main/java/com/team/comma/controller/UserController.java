package com.team.comma.controller;

import javax.security.auth.login.AccountException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.team.comma.dto.LoginRequest;
import com.team.comma.dto.MessageResponse;
import com.team.comma.dto.RegisterRequest;
import com.team.comma.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class UserController {

	final private UserService userService;
	
	@PostMapping(value = "/login")
	public MessageResponse loginUser(@RequestBody LoginRequest login) throws AccountException {
		return userService.login(login);
	}
	
	@PostMapping(value = "/register")
	public MessageResponse registerUser(@RequestBody RegisterRequest register) throws AccountException {
		return userService.register(register);
	}

}
