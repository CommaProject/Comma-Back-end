package com.team.comma.domain.user.detail.controller;

import com.team.comma.domain.user.detail.dto.UserDetailRequest;
import com.team.comma.domain.user.detail.service.UserDetailService;
import com.team.comma.global.common.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;

@RequestMapping("/user/detail")
@RestController
@RequiredArgsConstructor
public class UserDetailController {

    private final UserDetailService userDetailService;

    @PostMapping
    public ResponseEntity<MessageResponse> createUserDetail(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            @RequestBody UserDetailRequest request) throws AccountException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userDetailService.createUserDetail(accessToken, request));
    }

    @PatchMapping
    public ResponseEntity<MessageResponse> modifyUserDetail(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            @RequestBody UserDetailRequest request) throws AccountException {
        return ResponseEntity.ok()
                .body(userDetailService.modifyUserDetail(accessToken, request));
    }

}
