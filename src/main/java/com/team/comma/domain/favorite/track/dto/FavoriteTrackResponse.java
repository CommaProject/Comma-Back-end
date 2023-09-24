package com.team.comma.domain.favorite.track.dto;

import com.team.comma.domain.favorite.track.domain.FavoriteTrack;
import com.team.comma.domain.track.artist.dto.TrackArtistResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteTrackResponse {
    private long favoriteTrackId;

    private List<TrackArtistResponse> trackArtistResponses;

    public FavoriteTrackResponse(FavoriteTrack favoriteTrack, List<TrackArtistResponse> trackArtistList) {
        this.favoriteTrackId = favoriteTrack.getId();
        this.trackArtistResponses = trackArtistList;
    }

    public static FavoriteTrackResponse of(FavoriteTrack favoriteTrack, List<TrackArtistResponse> trackArtistList) {
        return new FavoriteTrackResponse(favoriteTrack, trackArtistList);
    }

}
