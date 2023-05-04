package com.team.comma.spotify.search.controller;

import com.team.comma.spotify.search.dto.RequestResponse;
import com.team.comma.spotify.search.service.SpotifySearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;

@RequestMapping(value = "/spotify")
@RestController
@RequiredArgsConstructor
public class SpotifySearchController {

    final private SpotifySearchService spotifyService;

    @GetMapping("/artist/{artist}")
    public ResponseEntity<RequestResponse> getArtist(@PathVariable String artist , @CookieValue("accessToken") String accessToken) throws AccountException {
        return ResponseEntity.ok().body(spotifyService.searchArtist_Sync(artist , accessToken));
    }

    @GetMapping("/track/{track}")
    public ResponseEntity<RequestResponse> getTrack(@PathVariable String track , @CookieValue("accessToken") String accessToken) throws AccountException {
        return ResponseEntity.ok().body(spotifyService.searchTrack_Sync(track , accessToken));
    }

    @GetMapping("/genre")
    public ResponseEntity<RequestResponse> getGenres() {
        return ResponseEntity.ok().body(spotifyService.searchGenres());
    }

    @GetMapping("/artist")
    public ResponseEntity<RequestResponse> getArtistByYear(@RequestParam int offset) {
        return ResponseEntity.ok().body(spotifyService.searchArtistByYear(offset));
    }
}
