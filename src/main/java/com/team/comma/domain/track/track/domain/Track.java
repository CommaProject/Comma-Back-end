package com.team.comma.domain.track.track.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.track.artist.domain.TrackArtist;
import jakarta.persistence.*;
import lombok.*;
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

    @Column(length = 150 , nullable = false)
    private String spotifyTrackHref;

    @OneToMany(mappedBy = "track" , cascade = CascadeType.PERSIST)
    @Builder.Default
    @JsonIgnore
    private List<TrackArtist> trackArtistList = new ArrayList<>();

    public void addTrackArtistList(Artist artist) {
        TrackArtist trackArtist = TrackArtist.builder()
            .artist(artist)
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
