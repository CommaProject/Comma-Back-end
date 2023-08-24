package com.team.comma.domain.track.domain;

import jakarta.persistence.*;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;

import java.util.ArrayList;
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
    @JsonIgnore
    private Long id;

    @Column(length = 30 , nullable = false)
    private String trackTitle;

    private Integer durationTimeMs;

    @Builder.Default
    private Long recommendCount = 0L;

    @Column(length = 100 , nullable = false)
    private String albumImageUrl;

    @Column(length = 50 , nullable = false)
    private String spotifyTrackId;

    @Column(length = 50 , nullable = false)
    private String spotifyTrackHref;

    @OneToMany(mappedBy = "track")
    @Builder.Default
    private List<TrackArtist> trackArtistList = new ArrayList<>();

    public void addTrackArtistList(String artistName) {
        TrackArtist trackArtist = TrackArtist.builder()
            .artistName(artistName)
            .track(this)
            .build();

        trackArtistList.add(trackArtist);
    }

    public static Track buildTrack(se.michaelthelin.spotify.model_objects.specification.Track track) {
        List<TrackArtist> trackArtists = new ArrayList<>();
        Track trackEntity = Track.builder()
                .trackTitle(track.getName())
                .durationTimeMs(track.getDurationMs())
                .albumImageUrl(track.getAlbum().getImages()[0].getUrl())
                .spotifyTrackId(track.getId())
                .spotifyTrackHref(track.getHref())
                .trackArtistList(trackArtists)
                .build();

        for(ArtistSimplified artistSimplified : track.getArtists()) {
            trackArtists.add(TrackArtist.createTrackArtist(artistSimplified , trackEntity));
        }

        return trackEntity;
    }

}
