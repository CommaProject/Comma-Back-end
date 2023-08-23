package com.team.comma.domain.playlist.controller;

import com.team.comma.domain.playlist.dto.track.PlaylistTrackRequest;
import com.team.comma.domain.playlist.dto.track.PlaylistTrackSaveRequestDto;
import com.team.comma.domain.playlist.service.PlaylistTrackService;
import com.team.comma.global.common.dto.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PlaylistTrackController {

    private final PlaylistTrackService playlistTrackService;

    @DeleteMapping("/playlist-track")
    public ResponseEntity<MessageResponse> disconnectPlaylistAndTrack
        (
            @Valid @RequestBody final PlaylistTrackRequest playlistTrackRequest
        ) {
        return ResponseEntity.ok(
            playlistTrackService.removePlaylistAndTrack(
                playlistTrackRequest.getTrackIdList(),
                playlistTrackRequest.getPlaylistId()
            )
        );
    }

    @PostMapping("/playlists/tracks")
    public ResponseEntity<MessageResponse> createPlaylistTrack(
        @CookieValue(value = "accessToken") String accessToken,
        @RequestBody final PlaylistTrackSaveRequestDto requestDto
    ) throws Exception {
        return ResponseEntity.ok(
            playlistTrackService.savePlaylistTrackList(requestDto, accessToken)
        );
    }

    @GetMapping("/playlists/tracks/{playlistId}")
    public ResponseEntity<MessageResponse> getPlaylistTracks(
            @PathVariable("playlistId") final long playlistId) {
        return ResponseEntity.ok().body(playlistTrackService.getPlaylistTracks(playlistId));
    }

}
