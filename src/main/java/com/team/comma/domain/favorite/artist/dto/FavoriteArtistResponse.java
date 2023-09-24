package com.team.comma.domain.favorite.artist.dto;

import com.team.comma.domain.artist.dto.ArtistResponse;
import com.team.comma.domain.favorite.artist.domain.FavoriteArtist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteArtistResponse {
    private long favoriteArtistId;
    private ArtistResponse artistResponse;

    private FavoriteArtistResponse(FavoriteArtist favoriteArtist) {
        this.favoriteArtistId = favoriteArtist.getId();
        this.artistResponse = ArtistResponse.createArtistResponse(favoriteArtist.getArtist());
    }

    public static FavoriteArtistResponse of(FavoriteArtist favoriteArtist) {
        return new FavoriteArtistResponse(favoriteArtist);
    }
}
