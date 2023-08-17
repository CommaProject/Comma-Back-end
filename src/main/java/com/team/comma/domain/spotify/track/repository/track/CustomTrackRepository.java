package com.team.comma.domain.spotify.track.repository.track;

import com.team.comma.domain.spotify.track.domain.Track;

import java.util.List;

public interface CustomTrackRepository {

    void updateTrackRecommendCount(String trackId);

    List<Track> findTrackMostRecommended();

}
