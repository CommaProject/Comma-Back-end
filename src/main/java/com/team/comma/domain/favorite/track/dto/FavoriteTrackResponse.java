package com.team.comma.domain.favorite.track.dto;

import com.team.comma.domain.favorite.track.domain.FavoriteTrack;
import com.team.comma.domain.track.artist.dto.TrackArtistResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteTrackResponse {
    private long favoriteTrackId;

    private long trackId;
    private String trackTitle;
    private String trackAlbumImageUrl;
    private String spotifyTrackId;

    private List<TrackArtistResponse> trackArtistList;

    private FavoriteTrackResponse(FavoriteTrack favoriteTrack, List<TrackArtistResponse> trackArtistList) {
        this.favoriteTrackId = favoriteTrack.getId();
        this.trackId = favoriteTrack.getTrack().getId();
        this.trackTitle = favoriteTrack.getTrack().getTrackTitle();
        this.trackAlbumImageUrl = favoriteTrack.getTrack().getAlbumImageUrl();
        this.spotifyTrackId = favoriteTrack.getTrack().getSpotifyTrackId();
        this.trackArtistList = new ArrayList<>(trackArtistList);
    }

    public static FavoriteTrackResponse of(FavoriteTrack favoriteTrack, List<TrackArtistResponse> trackArtistList) {
        return new FavoriteTrackResponse(favoriteTrack, trackArtistList);
    }

}
