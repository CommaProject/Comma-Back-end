package com.team.comma.spotify.playlist.controller;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.Exception.PlaylistException;
import com.team.comma.spotify.playlist.dto.PlaylistResponse;
import com.team.comma.spotify.playlist.dto.PlaylistRequest;
import com.team.comma.spotify.playlist.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlaylistController {

    final private PlaylistService playlistService;

    @GetMapping("/playlist")
    public ResponseEntity<List<PlaylistResponse>> getUserPlaylist(
            @RequestHeader final String email) {
        return ResponseEntity.ok().body(playlistService.getPlaylist(email));
    }

    @PostMapping("/playlist/update/alarm")
    public ResponseEntity<MessageResponse> updateAlarmFlag(
            @RequestBody final PlaylistRequest request) throws PlaylistException {
        return ResponseEntity.ok().body(playlistService.updateAlarmFlag(request.getPlaylistId(), request.isAlarmFlag()));
    }
}
