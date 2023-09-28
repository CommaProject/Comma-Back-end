package com.team.comma.spotify.controller;

import com.team.comma.spotify.service.SearchService;
import com.team.comma.global.message.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;

@RequestMapping(value = "/spotify")
@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService spotifyService;

    @GetMapping("/artists/{artist-name}")
    public ResponseEntity<MessageResponse> searchArtistList(
            @PathVariable("artist-name") String artistName,
            @CookieValue("accessToken") String accessToken) throws AccountException {
        return ResponseEntity.ok().body(spotifyService.searchArtistList(artistName , accessToken));
    }

    @GetMapping("/artist/{id}/tracks")
    public ResponseEntity<MessageResponse> searchTrackListByArtist(
            @PathVariable String id,
            @CookieValue("accessToken") String accessToken) throws AccountException {
        return ResponseEntity.ok().body(spotifyService.searchTrackListByArtist(id, accessToken));
    }

    @GetMapping("/tracks/{track-name}")
    public ResponseEntity<MessageResponse> searchTrackList(
            @PathVariable("track-name") String trackName,
            @CookieValue("accessToken") String accessToken) throws AccountException {
        return ResponseEntity.ok().body(spotifyService.searchTrackList(trackName , accessToken));
    }

    @GetMapping("/genre")
    public ResponseEntity<MessageResponse> searchGenreList() {
        return ResponseEntity.ok().body(spotifyService.searchGenreList());
    }

    @GetMapping("/artist")
    public ResponseEntity<MessageResponse> searchArtistListByYear(
            @RequestParam int offset) {
        return ResponseEntity.ok().body(spotifyService.searchArtistListByYear(offset));
    }

}
