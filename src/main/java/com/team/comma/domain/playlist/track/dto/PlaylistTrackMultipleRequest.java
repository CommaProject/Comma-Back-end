package com.team.comma.domain.playlist.track.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistTrackMultipleRequest {

    private List<Long> playlistTrackIdList;

}
