package com.team.comma.spotify.dto;


import lombok.*;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Image;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class SearchArtistResponse {

    private String artistId;
    private String artistName;
    private String[] genres;
    private Image[] images;
    private int popularity;

    public static SearchArtistResponse createArtistResponse(Artist artist) {
        return SearchArtistResponse.builder()
            .artistId(artist.getId())
            .artistName(artist.getName())
            .genres(artist.getGenres())
            .images(artist.getImages())
            .popularity(artist.getPopularity())
            .build();
    }
}
