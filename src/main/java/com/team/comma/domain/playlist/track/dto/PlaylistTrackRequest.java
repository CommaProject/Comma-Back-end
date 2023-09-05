package com.team.comma.domain.playlist.track.dto;

import java.util.List;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistTrackRequest {

    private List<Long> playlistIdList;
    private String spotifyTrackId;

}
