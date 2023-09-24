package com.team.comma.domain.artist.dto;

import com.team.comma.domain.artist.domain.Artist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArtistResponse {

    private String spotifyArtistId;

    private String artistName;

    public static ArtistResponse createArtistResponse(Artist artist) {
        return ArtistResponse.builder()
                .spotifyArtistId(artist.getSpotifyArtistId())
                .artistName(artist.getArtistName())
                .build();
    }
}
