package com.team.comma.domain.track.playcount.dto;

import lombok.*;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class TrackPlayCountRequest {

    private String spotifyTrackId;

    public static TrackPlayCountRequest of() {
        return TrackPlayCountRequest.builder()
                .spotifyTrackId("spotifyTrackId")
                .build();
    }
}
