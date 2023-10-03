package com.team.comma.domain.track.artist.domain;

import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.track.track.domain.Track;
import jakarta.persistence.*;
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

}
