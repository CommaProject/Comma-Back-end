package com.team.comma.domain.favorite.artist.dto;

import lombok.*;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class FavoriteArtistRequest {
    private String artistName;
//    private long favoriteArtistId;
}
