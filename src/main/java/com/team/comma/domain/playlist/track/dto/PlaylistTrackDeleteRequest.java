package com.team.comma.domain.playlist.track.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistTrackDeleteRequest {

    private long playlistId;
    private Set<Long> trackIdList;

}
