package com.team.comma.domain.playlist.track.controller;

import com.team.comma.domain.playlist.track.dto.PlaylistTrackMultipleRequest;
import com.team.comma.domain.playlist.track.dto.PlaylistTrackSingleRequest;
import com.team.comma.domain.playlist.track.dto.PlaylistTrackRequest;
import com.team.comma.domain.playlist.track.service.PlaylistTrackService;
import com.team.comma.global.message.MessageResponse;
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
            @RequestBody final PlaylistTrackRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(playlistTrackService.createPlaylistTrack(request.getPlaylistIdList(), request.getSpotifyTrackId()));
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<MessageResponse> findPlaylistTrack(
            @PathVariable("playlistId") final long playlistId) {
        return ResponseEntity.ok()
                .body(playlistTrackService.findPlaylistTrack(playlistId));
    }

    @PatchMapping("/sequence")
    public ResponseEntity<MessageResponse> modifyPlaylistTrackSequence(
            @RequestBody final PlaylistTrackMultipleRequest request){
        return ResponseEntity.ok()
                .body(playlistTrackService.modifyPlaylistTrackSequence(request.getPlaylistTrackIdList()));
    }

    @PatchMapping("/alert")
    public ResponseEntity<MessageResponse> modifyPlaylistTrackAlarmFlag(
            @RequestBody final PlaylistTrackSingleRequest request){
        return ResponseEntity.ok()
                .body(playlistTrackService.modifyPlaylistTrackAlarmFlag(request.getPlaylistTrackId()));
    }

    @DeleteMapping
    public ResponseEntity<MessageResponse> deletePlaylistTracks(
            @RequestBody final PlaylistTrackMultipleRequest request) {
        return ResponseEntity.ok()
                .body(playlistTrackService.deletePlaylistTracks(request.getPlaylistTrackIdList()));
    }

}
