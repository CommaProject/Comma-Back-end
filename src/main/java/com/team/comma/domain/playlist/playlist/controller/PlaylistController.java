package com.team.comma.domain.playlist.playlist.controller;

import com.team.comma.domain.playlist.playlist.exception.PlaylistException;
import com.team.comma.domain.playlist.playlist.dto.PlaylistRequest;
import com.team.comma.domain.playlist.playlist.dto.PlaylistUpdateRequest;
import com.team.comma.domain.playlist.playlist.service.PlaylistService;
import com.team.comma.global.common.dto.MessageResponse;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/playlist")
public class PlaylistController {

    private final PlaylistService playlistService;

    @GetMapping
    public ResponseEntity<MessageResponse> findAllPlaylists(
        @CookieValue final String accessToken) throws AccountException {

        return ResponseEntity.ok().body(playlistService.findAllPlaylists(accessToken));
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<MessageResponse> findPlaylist(@PathVariable long playlistId) {

        return ResponseEntity.ok().body(playlistService.findPlaylist(playlistId));
    }

    @PatchMapping
    public ResponseEntity<MessageResponse> modifyPlaylist(
            @RequestBody final PlaylistUpdateRequest playlistUpdateRequest) {

        return ResponseEntity.ok(playlistService.modifyPlaylist(playlistUpdateRequest));
    }

    @PatchMapping("/alert")
    public ResponseEntity<MessageResponse> modifyPlaylistAlarmFlag(
            @RequestBody final PlaylistRequest request) throws PlaylistException {

        return ResponseEntity.ok().body(playlistService.modifyPlaylistAlarmFlag(request.getPlaylistId(), request.isAlarmFlag()));
    }

    @DeleteMapping
    public ResponseEntity<MessageResponse> deletePlaylist(
            @RequestBody final List<Long> playlistIdList) throws PlaylistException {

        return ResponseEntity.ok().body(playlistService.modifyPlaylistsDelFlag(playlistIdList));
    }

    @GetMapping("/total-duration-time/{id}")
    public ResponseEntity<MessageResponse> findTotalDurationTimeMsByPlaylist(
            @PathVariable("id") final Long id) {

        return ResponseEntity.ok(playlistService.findTotalDurationTimeMsByPlaylist(id));
    }
}