package com.team.comma.domain.track.playcount.controller;

import com.team.comma.domain.track.playcount.dto.TrackPlayCountRequest;
import com.team.comma.domain.track.playcount.service.PlayCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;

@RequestMapping(value = "/track/play-count")
@RestController
@RequiredArgsConstructor
public class PlayCountController {

    private final PlayCountService playCountService;

    @PostMapping
    public ResponseEntity creatTrackPlay(
            @CookieValue String accessToken,
            @RequestBody TrackPlayCountRequest trackPlayCountRequest) throws AccountException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(playCountService.createTrackPlay(accessToken, trackPlayCountRequest.getSpotifyTrackId()));
    }

    @GetMapping
    public ResponseEntity findMostListenedTrack(@CookieValue String accessToken) {
        return ResponseEntity.ok()
                .body(playCountService.findMostListenedTrack(accessToken));
    }

    @GetMapping("/friends")
    public ResponseEntity findMostedTrackByFriend(@CookieValue String accessToken) {
        return ResponseEntity.ok()
                .body(playCountService.findMostListenedTrackByFriend(accessToken));
    }

}
