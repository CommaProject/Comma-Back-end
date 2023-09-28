package com.team.comma.domain.playlist.recommend.controller;

import com.team.comma.domain.playlist.recommend.constant.RecommendListType;
import com.team.comma.domain.playlist.recommend.dto.RecommendRequest;
import com.team.comma.domain.playlist.recommend.service.RecommendService;
import com.team.comma.global.message.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommend")
public class RecommendController {

    private final RecommendService recommendService;

    @PostMapping
    public ResponseEntity<MessageResponse> createPlaylistRecommend(
            @CookieValue final String accessToken,
            @RequestBody final RecommendRequest recommendRequest
            ) throws AccountException {
        return ResponseEntity.ok().body(
                recommendService.createPlaylistRecommend(accessToken, recommendRequest));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<MessageResponse> findAllPlaylistRecommends(
            @CookieValue final String accessToken,
            @PathVariable final RecommendListType type
    ) throws AccountException {
        return ResponseEntity.ok().body(
                recommendService.findAllPlaylistRecommends(accessToken, type));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponse> findPlaylistRecommend(
            @CookieValue final String accessToken,
            @PathVariable final long id
    ) {
        return ResponseEntity.ok().body(
                recommendService.findRecommend(accessToken, id));
    }

}
