package com.team.comma.domain.track.artist.domain;

import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.track.track.domain.Track;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;

import static com.team.comma.domain.artist.domain.Artist.createArtist;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "track_artist_tb")
public class TrackArtist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @JoinColumn(name = "artist_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Artist artist;

    @Setter
    @JoinColumn(name = "track_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Track track;

    public static TrackArtist createTrackArtist(ArtistSimplified artist, Track track) {
        return TrackArtist.builder()
                .artist(createArtist(artist.getName()))
                .track(track)
                .build();
    }

}
