package com.team.comma.spotify.track.controller;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.track.dto.TrackRequest;
import com.team.comma.spotify.track.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;

@RequestMapping(value = "/tracks")
@RestController
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;

    @PatchMapping("/{trackId}")
    public MessageResponse countPlayCount(@CookieValue String accessToken , @PathVariable String trackId) {
        return trackService.countPlayCount(accessToken , trackId);
    }

    @PostMapping("/favorites")
    public MessageResponse addFavoriteTrack(@CookieValue String accessToken , @RequestBody TrackRequest trackRequest) throws AccountException {
        return trackService.addFavoriteTrack(accessToken , trackRequest);
    }

    @GetMapping
    public MessageResponse findMostListenedTrack(@CookieValue String accessToken) {
        return trackService.findMostListenedTrack(accessToken);
    }

    @GetMapping("/friends")
    public MessageResponse findMostListenedTrackByFriend(@CookieValue String accessToken) {
        return trackService.findMostListenedTrackByFriend(accessToken);
    }

    @GetMapping("/users/favorites")
    public MessageResponse findTrackByFavoriteTrack(@CookieValue String accessToken) {
        return trackService.findTrackByFavoriteTrack(accessToken);
    }

    @GetMapping("/favorites")
    public MessageResponse findTrackByMostFavorite() {
        return trackService.findTrackByMostFavorite();
    }

}


