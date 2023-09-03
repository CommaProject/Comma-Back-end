package com.team.comma.domain.playlist.track.controller;

import com.team.comma.domain.playlist.track.dto.PlaylistTrackDeleteRequest;
import com.team.comma.domain.playlist.track.dto.PlaylistTrackRequest;
import com.team.comma.domain.playlist.track.service.PlaylistTrackService;
import com.team.comma.global.common.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/playlist/track")
@RestController
@RequiredArgsConstructor
public class PlaylistTrackController {

    private final PlaylistTrackService playlistTrackService;

    @PostMapping
    public ResponseEntity<MessageResponse> createPlaylistTrack(
            @RequestBody final PlaylistTrackRequest playlistTrackRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(playlistTrackService.createPlaylistTrack(playlistTrackRequest));
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<MessageResponse> findPlaylistTrack(
            @PathVariable("playlistId") final long playlistId) {
        return ResponseEntity.ok()
                .body(playlistTrackService.findPlaylistTrack(playlistId));
    }

    @DeleteMapping
    public ResponseEntity<MessageResponse> deletePlaylistTrack(
            @RequestBody final PlaylistTrackDeleteRequest playlistTrackRequest) {
        return ResponseEntity.ok()
                .body(playlistTrackService.removePlaylistAndTrack(playlistTrackRequest.getTrackIdList(), playlistTrackRequest.getPlaylistId()));
    }

}
