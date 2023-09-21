package com.team.comma.domain.playlist.playlist.controller;

import com.team.comma.domain.playlist.playlist.dto.PlaylistModifyRequest;
import com.team.comma.domain.playlist.playlist.dto.PlaylistRequest;
import com.team.comma.domain.playlist.playlist.exception.PlaylistException;
import com.team.comma.domain.playlist.playlist.service.PlaylistService;
import com.team.comma.global.common.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/playlist")
@RestController
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping
    public ResponseEntity<MessageResponse> createPlaylist(
            @CookieValue final String accessToken,
            @RequestBody final PlaylistRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(playlistService.createPlaylist(accessToken, request.getSpotifyTrackId()));
    }

    @GetMapping
    public ResponseEntity<MessageResponse> findAllPlaylists(
        @CookieValue final String accessToken) {
        return ResponseEntity.ok()
                .body(playlistService.findAllPlaylists(accessToken));
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<MessageResponse> findPlaylist(
            @PathVariable long playlistId) {
        return ResponseEntity.ok()
                .body(playlistService.findPlaylist(playlistId));
    }

    @GetMapping("/total-duration-time/{playlistId}")
    public ResponseEntity<MessageResponse> findTotalDurationTimeMsByPlaylist(
            @PathVariable("playlistId") final Long playlistId) {
        return ResponseEntity.ok()
                .body(playlistService.findTotalDurationTimeMsByPlaylist(playlistId));
    }

    @PatchMapping("/title")
    public ResponseEntity<MessageResponse> modifyPlaylistTitle(
            @RequestBody final PlaylistModifyRequest request) {
        return ResponseEntity.ok()
                .body(playlistService.modifyPlaylistTitle(request));
    }

    @PatchMapping("/alert")
    public ResponseEntity<MessageResponse> modifyPlaylistAlarmFlag(
            @RequestBody final PlaylistModifyRequest request) throws PlaylistException {
        return ResponseEntity.ok()
                .body(playlistService.modifyPlaylistAlarmFlag(request));
    }

    @PatchMapping("/alert/day-time")
    public ResponseEntity<MessageResponse> modifyPlaylistAlarmDayAndTime(
            @RequestBody final PlaylistModifyRequest request) {
        return ResponseEntity.ok()
                .body(playlistService.modifyPlaylistAlarmDayAndTime(request));
    }

    @DeleteMapping
    public ResponseEntity<MessageResponse> deletePlaylist(
            @RequestBody final List<Long> playlistIdList) throws PlaylistException {
        return ResponseEntity.ok()
                .body(playlistService.modifyPlaylistsDelFlag(playlistIdList));
    }

}