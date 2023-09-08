package com.team.comma.domain.track.track.dto;

import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.track.track.domain.Track;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrackArtistResponse {

    private Track track;

    private List<Artist> artists;

    public static TrackArtistResponse of(Track track , List<Artist> artists) {
        return TrackArtistResponse.builder()
                .track(track)
                .artists(artists)
                .build();
    }
}
