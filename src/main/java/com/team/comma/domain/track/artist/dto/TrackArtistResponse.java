package com.team.comma.domain.track.artist.dto;

import com.team.comma.domain.artist.dto.ArtistResponse;
import com.team.comma.domain.track.track.dto.TrackResponse;
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

    private ArtistResponse artist;

    public static TrackArtistResponse of(TrackResponse track, ArtistResponse artist) {
        return TrackArtistResponse.builder()
                .track(track)
                .artist(artist)
                .build();
    }

}
