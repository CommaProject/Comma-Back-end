package com.team.comma.domain.playlist.track.dto;

import com.team.comma.domain.track.artist.TrackArtist;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class PlaylistTrackArtistResponse {

    private final long artistId;
    private final String artistName;

    private PlaylistTrackArtistResponse(TrackArtist trackArtist) {
        this.artistId = trackArtist.getId();
        this.artistName = trackArtist.getArtistName();
    }

    public static PlaylistTrackArtistResponse of(TrackArtist trackArtist) {
        return new PlaylistTrackArtistResponse(trackArtist);
    }

}
