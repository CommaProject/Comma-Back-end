package com.team.comma.domain.spotify.playlist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistRequest {
    private long playlistId;
    private boolean alarmFlag;
}
