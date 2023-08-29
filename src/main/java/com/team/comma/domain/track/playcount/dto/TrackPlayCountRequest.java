package com.team.comma.domain.track.playcount.dto;

import com.team.comma.domain.track.artist.domain.TrackArtist;
import com.team.comma.domain.track.track.domain.Track;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class TrackPlayCountRequest {
    private String trackId;

    private String trackImageUrl;

    private String trackName;

    private String trackArtist;

    public static TrackPlayCountRequest createTrackPlayCountRequest(Track track) {

        return TrackPlayCountRequest.builder()
                .trackId(track.getSpotifyTrackId())
                .trackImageUrl(track.getAlbumImageUrl())
                .trackName(track.getTrackTitle())
                .trackArtist(track.getTrackArtistList().get(0).getArtistName())
                .build();
    }
}
