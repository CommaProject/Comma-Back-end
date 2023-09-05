package com.team.comma.domain.playlist.track.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistTrackSingleRequest {

    private long playlistTrackId;

}
