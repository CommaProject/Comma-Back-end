package com.team.comma.domain.favorite.dto;

import lombok.*;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class FavoriteArtistRequest {
    private String artistName;
}
