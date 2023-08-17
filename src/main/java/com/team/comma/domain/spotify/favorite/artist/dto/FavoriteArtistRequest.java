package com.team.comma.domain.spotify.favorite.artist.dto;

import lombok.*;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class FavoriteArtistRequest {
    private String artistName;
}
