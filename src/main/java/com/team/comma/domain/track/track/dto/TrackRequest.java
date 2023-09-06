package com.team.comma.domain.track.track.dto;

import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.artist.domain.TrackArtist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TrackRequest {

    private String trackTitle;

    private String albumImageUrl;

    private String spotifyTrackId;

    private String spotifyTrackHref;

    private int durationTimeMs;

    private String artist;

}
