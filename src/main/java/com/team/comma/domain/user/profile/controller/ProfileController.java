package com.team.comma.domain.user.profile.controller;


import com.team.comma.domain.user.profile.dto.UserDetailRequest;
import com.team.comma.domain.user.profile.service.ProfileService;
import com.team.comma.domain.user.user.service.UserService;
import com.team.comma.global.common.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<MessageResponse> createProfile(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            @RequestBody UserDetailRequest userDetail) throws AccountException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(profileService.createProfile(userDetail, accessToken));
    }

}
