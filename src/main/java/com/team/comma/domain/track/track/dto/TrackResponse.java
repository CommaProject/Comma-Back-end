package com.team.comma.domain.track.track.dto;

import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.artist.dto.ArtistResponse;
import com.team.comma.domain.track.track.domain.Track;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackResponse {

    private Long id;
    private String trackTitle;

    private Integer durationTimeMs;
    private Long recommendCount;
    private String albumImageUrl;
    private String spotifyTrackId;
    private String spotifyTrackHref;

    public static TrackResponse of(Track track) {
        return TrackResponse.builder()
                .id(track.getId())
                .trackTitle(track.getTrackTitle())
                .durationTimeMs(track.getDurationTimeMs())
                .recommendCount(track.getRecommendCount())
                .albumImageUrl(track.getAlbumImageUrl())
                .spotifyTrackId(track.getSpotifyTrackId())
                .spotifyTrackHref(track.getSpotifyTrackHref())
                .build();
    }

}
