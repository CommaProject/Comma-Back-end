package com.team.comma.domain.track.track.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.artist.service.ArtistService;
import com.team.comma.domain.track.artist.domain.TrackArtist;
import jakarta.persistence.*;
import lombok.*;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;

import java.util.ArrayList;
import java.util.List;

import static com.team.comma.domain.track.artist.domain.TrackArtist.createTrackArtist;

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

    @Column(length = 150 , nullable = false)
    private String trackTitle;

    private Integer durationTimeMs;

    @Builder.Default
    private Long recommendCount = 0L;

    @Column(length = 100 , nullable = false)
    private String albumImageUrl;

    @Column(length = 100 , nullable = false)
    private String spotifyTrackId;

    @Column(length = 150 , nullable = false)
    private String spotifyTrackHref;

    @OneToMany(mappedBy = "track" , cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<TrackArtist> trackArtistList = new ArrayList<>();

    public void addTrackArtistList(Artist artist) {
        TrackArtist trackArtist = TrackArtist.builder()
            .artist(artist)
            .track(this)
            .build();

        trackArtistList.add(trackArtist);
    }

    public static Track buildTrack(se.michaelthelin.spotify.model_objects.specification.Track track , ArtistService artistService) {
        Track trackEntity = Track.builder()
                .trackTitle(track.getName())
                .durationTimeMs(track.getDurationMs())
                .albumImageUrl(track.getAlbum().getImages()[0].getUrl())
                .spotifyTrackId(track.getId())
                .spotifyTrackHref(track.getHref())
                .build();


        for(ArtistSimplified artistSimplified : track.getArtists()) {
            Artist artist = artistService.findArtistOrSave(artistSimplified.getId() , artistSimplified.getName());
            trackEntity.addTrackArtistList(artist);
        }

        return trackEntity;
    }

}
