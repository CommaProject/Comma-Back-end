package com.team.comma.domain.track.track.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackPlayCountResponse {
    private Integer playCount;

    private String trackId;

    private String trackImageUrl;

    private String trackName;

    private String trackArtist;
}
