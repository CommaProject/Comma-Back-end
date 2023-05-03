package com.team.comma.spotify.search.controller;

import com.team.comma.spotify.search.dto.RequestResponse;
import com.team.comma.spotify.search.service.SpotifySearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/spotify")
@RestController
@RequiredArgsConstructor
public class SpotifySearchController {

    final private SpotifySearchService spotifyService;

    @GetMapping("/artist/{artist}")
    public ResponseEntity<RequestResponse> getArtist(@PathVariable String artist) {
        return ResponseEntity.ok().body(spotifyService.searchArtist_Sync(artist));
    }

    @GetMapping("/track/{track}")
    public ResponseEntity<RequestResponse> getTrack(@PathVariable String track) {
        return ResponseEntity.ok().body(spotifyService.searchTrack_Sync(track));
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
