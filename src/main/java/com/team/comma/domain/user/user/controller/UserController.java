package com.team.comma.domain.user.user.controller;

import com.team.comma.domain.user.user.dto.LoginRequest;
import com.team.comma.domain.user.user.dto.RegisterRequest;
import com.team.comma.domain.user.user.service.UserService;
import com.team.comma.global.common.dto.MessageResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;


@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<MessageResponse> loginUser(
            @RequestBody LoginRequest login,
            HttpServletResponse response) throws AccountException {
        return ResponseEntity.ok().body(userService.login(login , response));
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> registerUser(
            @RequestBody RegisterRequest register) throws AccountException {
        return ResponseEntity.ok().body(userService.register(register));
    }

    @GetMapping("/user/information")
    public ResponseEntity<MessageResponse> getUserInfoByEmail(
        @CookieValue("accessToken") String accessToken) {
        return ResponseEntity.ok().body(userService.getUserByCookie(accessToken));
    }

    @GetMapping("/search/user")
    public ResponseEntity<MessageResponse> searchUserByNameAndNickName(
            @RequestParam String name,
            @CookieValue("accessToken") String accessToken) {
        return ResponseEntity.ok().body(userService.searchUserByNameAndNickName(name , accessToken));
    }

}
