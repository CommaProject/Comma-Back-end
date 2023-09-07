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
    private String artistId;

    @Column(length = 30)
    private String artistName;

    public static Artist createArtist(String artistId , String artistName) {
        return Artist.builder()
                .artistId(artistId)
                .artistName(artistName)
                .build();
    }
}
