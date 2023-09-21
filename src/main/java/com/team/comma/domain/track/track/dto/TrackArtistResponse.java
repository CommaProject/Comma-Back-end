package com.team.comma.domain.track.track.dto;

import com.team.comma.domain.artist.dto.ArtistResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrackArtistResponse {

    private TrackResponse track;

    private ArtistResponse artists;

    public static TrackArtistResponse of(TrackResponse track , ArtistResponse artists) {
        return TrackArtistResponse.builder()
                .track(track)
                .artists(artists)
                .build();
    }
}
