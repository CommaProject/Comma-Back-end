package com.team.comma.domain.artist.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "artist_tb")
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String spotifyArtistId;

    @Column(length = 30)
    private String artistName;

    public static Artist createArtist(String spotifyArtistId , String artistName) {
        return Artist.builder()
                .spotifyArtistId(spotifyArtistId)
                .artistName(artistName)
                .build();
    }

    public static Artist createArtistWithSpotifyArtist(se.michaelthelin.spotify.model_objects.specification.Artist artist) {
        return Artist.builder()
                .spotifyArtistId(artist.getId())
                .artistName(artist.getName())
                .build();
    }
}
