package com.team.comma.domain.favorite.track.controller;

import com.team.comma.domain.favorite.track.dto.FavoriteTrackRequest;
import com.team.comma.domain.favorite.track.service.FavoriteTrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/favorite/track")
public class FavoriteTrackController {

    private final FavoriteTrackService favoriteTrackService;

    @PostMapping
    public ResponseEntity createFavoriteTrack(
            @CookieValue String accessToken,
            @RequestBody FavoriteTrackRequest favoriteTrackRequest) {
        return ResponseEntity.ok()
                .body(favoriteTrackService.createFavoriteTrack(accessToken, favoriteTrackRequest));
    }

    @GetMapping
    public ResponseEntity findAllFavoriteTrack(
            @CookieValue String accessToken) {
        return ResponseEntity.ok()
                .body(favoriteTrackService.findAllFavoriteTrack(accessToken));
    }

}
