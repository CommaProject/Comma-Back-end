package com.team.comma.domain.track.playcount.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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
