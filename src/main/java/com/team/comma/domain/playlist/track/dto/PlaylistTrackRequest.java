package com.team.comma.domain.playlist.track.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.track.domain.PlaylistTrack;
import com.team.comma.domain.track.track.dto.TrackRequest;
import com.team.comma.domain.user.user.domain.User;
import java.time.LocalTime;
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
