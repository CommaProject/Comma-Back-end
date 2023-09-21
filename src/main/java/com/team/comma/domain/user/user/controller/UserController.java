package com.team.comma.domain.user.user.controller;

import com.team.comma.domain.user.user.dto.UserRequest;
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
            @RequestBody UserRequest request,
            HttpServletResponse response) throws AccountException {
        return ResponseEntity.ok().body(userService.login(request , response));
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> registerUser(
            @RequestBody UserRequest register) throws AccountException {
        return ResponseEntity.ok().body(userService.register(register));
    }

    @GetMapping("/user/information")
    public ResponseEntity<MessageResponse> getUserInformation(
            @CookieValue("accessToken") String accessToken) {
        return ResponseEntity.ok().body(userService.getUserInformation(accessToken));
    }

    @GetMapping("/user/{searchWord}")
    public ResponseEntity<MessageResponse> findAllUsersBySearchWord(
            @PathVariable String searchWord,
            @CookieValue("accessToken") String accessToken) throws AccountException {
        return ResponseEntity.ok().body(userService.findAllUsersBySearchWord(searchWord, accessToken));
    }

    @PatchMapping("/user")
    public ResponseEntity<MessageResponse> modifyUserPassword(
            @CookieValue String accessToken,
            @RequestBody UserRequest request) throws AccountException {
        return ResponseEntity.ok().body(userService.modifyUserPassword(accessToken, request.getPassword()));
    }

}
