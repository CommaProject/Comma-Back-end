package com.team.comma.domain.favorite.track.dto;

import com.team.comma.domain.favorite.track.domain.FavoriteTrack;
import lombok.*;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class FavoriteTrackRequest {

    private String spotifyTrackId;


    public static FavoriteTrackRequest buildFavoriteTrackRequest(String spotifyTrackId) {
        return FavoriteTrackRequest.builder()
                .spotifyTrackId(spotifyTrackId)
                .build();
    }
}
