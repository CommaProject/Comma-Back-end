package com.team.comma.domain.track.track.repository;

import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.track.artist.domain.TrackArtist;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.track.track.dto.TrackArtistResponse;

import java.util.List;
import java.util.Optional;

public interface TrackRepositoryCustom {

    void updateTrackRecommendCount(String trackId);

    List<TrackArtistResponse> findTrackMostRecommended();

}
