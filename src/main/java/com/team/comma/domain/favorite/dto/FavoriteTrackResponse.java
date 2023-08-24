package com.team.comma.domain.favorite.dto;

import com.team.comma.domain.favorite.domain.FavoriteTrack;
import com.team.comma.domain.track.domain.TrackArtist;
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
    private long favoriteTrackid;

    private long trackId;
    private String trackTitle;
    private String trackAlbumImageUrl;
    private String spotifyTrackId;

    private List<Long> trackArtistIdList;
    private List<String> trackArtistNameList;

    private FavoriteTrackResponse(FavoriteTrack favoriteTrack) {
        this.favoriteTrackid = favoriteTrack.getId();
        this.trackId = favoriteTrack.getTrack().getId();
        this.trackTitle = favoriteTrack.getTrack().getTrackTitle();
        this.trackAlbumImageUrl = favoriteTrack.getTrack().getAlbumImageUrl();
        this.spotifyTrackId = favoriteTrack.getTrack().getSpotifyTrackId();
        this.trackArtistIdList = new ArrayList<>(
                createTrackArtistIdList(favoriteTrack.getTrack().getTrackArtistList()));
        this.trackArtistNameList = new ArrayList<>(
                createTrackArtistNameList(favoriteTrack.getTrack().getTrackArtistList()));
    }

    public List<Long> createTrackArtistIdList(List<TrackArtist> trackArtists){
        List<Long> trackArtistIdList = new ArrayList<>();
        for(TrackArtist trackArtist : trackArtists){
            trackArtistIdList.add(trackArtist.getId());
        }
        return trackArtistIdList;
    }

    public List<String> createTrackArtistNameList(List<TrackArtist> trackArtists){
        List<String> trackArtistNameList = new ArrayList<>();
        for(TrackArtist trackArtist : trackArtists){
            trackArtistNameList.add(trackArtist.getArtistName());
        }
        return trackArtistNameList;
    }

    public static FavoriteTrackResponse of(FavoriteTrack favoriteTrack) {
        return new FavoriteTrackResponse(favoriteTrack);
    }

}
