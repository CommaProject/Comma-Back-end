package com.team.comma.spotify.recommend.controller;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.recommend.constant.RecommendListType;
import com.team.comma.spotify.recommend.dto.RecommendListRequest;
import com.team.comma.spotify.recommend.dto.RecommendRequest;
import com.team.comma.spotify.recommend.dto.RecommendResponse;
import com.team.comma.spotify.recommend.service.RecommendService;
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
    public ResponseEntity<MessageResponse> recommendPlaylist(
            @CookieValue final String accessToken,
            @RequestBody final RecommendRequest recommendRequest
            ) throws AccountException {
        return ResponseEntity.ok().body(
                recommendService.addRecommend(accessToken, recommendRequest));
    }

    @GetMapping
    public ResponseEntity<MessageResponse> recommendList(
            @CookieValue final String accessToken,
            @RequestBody final RecommendListRequest recommendListRequest
    ) throws AccountException {
        return ResponseEntity.ok().body(
                recommendService.getRecommendList(accessToken, recommendListRequest));
    }

    @GetMapping("/{recommendId}")
    public ResponseEntity<MessageResponse> recommendDetail(
            @PathVariable final long recommendId
    ) {
        return ResponseEntity.ok().body(
                recommendService.getRecommend(recommendId));
    }

}
