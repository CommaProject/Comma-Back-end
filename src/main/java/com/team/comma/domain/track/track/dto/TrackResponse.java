package com.team.comma.domain.track.track.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackResponse {

    private Long id;
    private String trackTitle;

    private Integer durationTimeMs;
    private Long recommendCount;
    private String albumImageUrl;
    private String spotifyTrackId;
    private String spotifyTrackHref;

}
