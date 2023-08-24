package com.team.comma.domain.user.following.controller;

import com.team.comma.domain.user.following.service.FollowingService;
import com.team.comma.domain.user.following.dto.FollowingRequest;
import com.team.comma.global.common.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/followings")
public class FollowingController {

    private final FollowingService followingService;

    @GetMapping
    public ResponseEntity<MessageResponse> isFollow(@CookieValue String accessToken
            , @RequestBody FollowingRequest followingRequest) {
        return ResponseEntity.ok()
                .body(followingService.isFollowedUser(accessToken , followingRequest.getToUserEmail()));
    }

    @PostMapping
    public ResponseEntity<MessageResponse> addFollow(@CookieValue String accessToken
            , @RequestBody FollowingRequest followingRequest) throws AccountException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(followingService.addFollow(accessToken , followingRequest.getToUserEmail()));
    }

    @DeleteMapping
    public ResponseEntity<MessageResponse> blockFollow(@CookieValue String accessToken
            , @RequestBody FollowingRequest followingRequest) {
        return ResponseEntity.ok()
                .body(followingService.blockFollowedUser(accessToken , followingRequest.getToUserEmail()));
    }

    @PatchMapping("/unblocks")
    public ResponseEntity<MessageResponse> unBlockFollow(@CookieValue String accessToken
            , @RequestBody FollowingRequest followingRequest) {
        return ResponseEntity.ok()
                .body(followingService.unblockFollowedUser(accessToken , followingRequest.getToUserEmail()));
    }

    @GetMapping("/list")
    public ResponseEntity<MessageResponse> getFollowingList(@CookieValue String accessToken
            , @RequestBody FollowingRequest followingRequest) throws AccountException {
        return ResponseEntity.ok()
                .body(followingService.getFollowingUserList(accessToken, followingRequest.getFollowingType()));
    }

}

