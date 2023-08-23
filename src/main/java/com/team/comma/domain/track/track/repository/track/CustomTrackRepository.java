package com.team.comma.domain.track.track.repository.track;

import com.team.comma.domain.track.track.domain.Track;

import java.util.List;

public interface CustomTrackRepository {

    void updateTrackRecommendCount(String trackId);

    List<Track> findTrackMostRecommended();

}
