package com.team.comma.controller;

import com.team.comma.dto.PlaylistResponse;
import com.team.comma.service.PlaylistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlaylistController {

    final private PlaylistService playlistService;

    @Operation(summary = "사용자 플레이리스트 조회", description = "사용자 이메일로 플레이리스트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PlaylistResponse.class)))
    })
    @GetMapping("/playlist")
    public ResponseEntity<List<PlaylistResponse>> getUserPlaylist(
            @RequestHeader("email") final String email) {
        return ResponseEntity.ok(playlistService.getPlaylistResponse(email));
    }

    @PostMapping("/playlist/alarm/update")
    public ResponseEntity<String> updateAlarmFlag(
            @RequestBody final Long id,
            @RequestBody final Boolean flag){
        int updated = playlistService.updateAlarmFlag(id,flag);

        if(updated > 0){
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
