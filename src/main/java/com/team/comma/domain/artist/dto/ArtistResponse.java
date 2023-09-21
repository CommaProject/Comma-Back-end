package com.team.comma.domain.artist.dto;

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

    private String spotifyArtistName;

    public static ArtistResponse createArtist(String artistId, String artist) {
        return ArtistResponse.builder()
                .spotifyArtistId(artistId)
                .spotifyArtistName(artist)
                .build();
    }
}
