package com.team.comma.domain.track.track.repository;

import com.team.comma.domain.track.artist.dto.TrackArtistResponse;

import java.util.List;

public interface TrackRepositoryCustom {

    void updateTrackRecommendCount(String trackId);

    List<TrackArtistResponse> findTrackMostRecommended();

}
