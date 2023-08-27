package com.team.comma.domain.favorite.track.dto;

import com.team.comma.domain.track.artist.domain.TrackArtist;
import com.team.comma.domain.track.track.domain.Track;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class FavoriteTrackRequest {

    private String spotifyTrackId;

}
