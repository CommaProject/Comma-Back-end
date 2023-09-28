package com.team.comma.domain.user.following.controller;

import com.team.comma.domain.user.following.constant.FollowingType;
import com.team.comma.domain.user.following.dto.FollowingRequest;
import com.team.comma.domain.user.following.service.FollowingService;
import com.team.comma.global.message.MessageResponse;
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

    @PostMapping
    public ResponseEntity<MessageResponse> addFollow(
            @CookieValue String accessToken,
            @RequestBody FollowingRequest followingRequest) throws AccountException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(followingService.addFollow(accessToken , followingRequest.getToUserId()));
    }

    @GetMapping("/type/{followingType}")
    public ResponseEntity<MessageResponse> getFollowingList(
            @CookieValue String accessToken,
            @PathVariable FollowingType followingType) {
        return ResponseEntity.ok()
                .body(followingService.getFollowingUserList(accessToken, followingType));
    }

    @GetMapping("/count")
    public ResponseEntity<MessageResponse> countFollowings(
            @CookieValue String accessToken) throws AccountException {
        return ResponseEntity.ok()
                .body(followingService.countFollowings(accessToken));
    }

    @GetMapping("/{toUserId}")
    public ResponseEntity<MessageResponse> isFollow(
            @CookieValue String accessToken,
            @PathVariable long toUserId) {
        return ResponseEntity.ok()
                .body(followingService.isFollowedUser(accessToken , toUserId));
    }

    @PatchMapping("/unblocks")
    public ResponseEntity<MessageResponse> unBlockFollow(
            @RequestBody FollowingRequest followingRequest) {
        return ResponseEntity.ok()
                .body(followingService.unblockFollowedUser(followingRequest.getFollowingId()));
    }

    @DeleteMapping
    public ResponseEntity<MessageResponse> blockFollow(
            @RequestBody FollowingRequest followingRequest) {
        return ResponseEntity.ok()
                .body(followingService.blockFollowedUser(followingRequest.getFollowingId()));
    }

}
