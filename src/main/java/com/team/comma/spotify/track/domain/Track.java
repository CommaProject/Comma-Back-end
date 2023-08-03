package com.team.comma.spotify.track.domain;

import jakarta.persistence.*;

import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "track_tb")
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30 , nullable = false)
    private String trackTitle;

    private Integer durationTimeMs;

    @Builder.Default
    private Long recommendCount = 0L;

    @Column(length = 50 , nullable = false)
    private String albumImageUrl;

    @Column(length = 50 , nullable = false)
    private String spotifyTrackId;

    @Column(length = 50 , nullable = false)
    private String spotifyTrackHref;

    @OneToMany(mappedBy = "track")
    private List<TrackArtist> trackArtistList;

    public void addTrackArtistList(String artistName) {
        TrackArtist trackArtist = TrackArtist.builder()
            .artistName(artistName)
            .track(this)
            .build();

        trackArtistList.add(trackArtist);
    }

}
