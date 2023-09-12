package com.team.comma.domain.user.detail.controller;


import com.team.comma.domain.user.detail.dto.UserDetailRequest;
import com.team.comma.domain.user.detail.service.UserDetailService;
import com.team.comma.global.common.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.login.AccountException;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class UserDetailController {

    private final UserDetailService userDetailService;

    @PostMapping
    public ResponseEntity<MessageResponse> createProfile(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            @RequestBody UserDetailRequest userDetail) throws AccountException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userDetailService.createProfile(userDetail, accessToken));
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<MessageResponse> uploadProfileImage(
            @CookieValue("accessToken") String accessToken,
            @RequestParam MultipartFile image) throws IOException {
        return ResponseEntity.ok().body(userDetailService.uploadProfileImage(accessToken, image));
    }

}
