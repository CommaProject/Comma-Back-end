package com.team.comma.domain.track.artist.dto;

import com.team.comma.domain.track.artist.domain.TrackArtist;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class TrackArtistResponse {

    private final long artistId;
    private final String artistName;

    private TrackArtistResponse(TrackArtist trackArtist) {
        this.artistId = trackArtist.getId();
        this.artistName = trackArtist.getArtist().getArtistName();
    }

    public static TrackArtistResponse of(TrackArtist trackArtist) {
        return new TrackArtistResponse(trackArtist);
    }

}
