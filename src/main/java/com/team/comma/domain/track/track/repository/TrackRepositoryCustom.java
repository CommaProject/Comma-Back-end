package com.team.comma.domain.track.track.repository;

import com.team.comma.domain.track.track.domain.Track;

import java.util.List;

public interface TrackRepositoryCustom {

    void updateTrackRecommendCount(String trackId);

    List<Track> findTrackMostRecommended();

}
