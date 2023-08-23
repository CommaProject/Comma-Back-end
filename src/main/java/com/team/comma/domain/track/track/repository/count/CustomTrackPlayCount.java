package com.team.comma.domain.track.track.repository.count;

import com.team.comma.domain.track.trackPlayCount.TrackPlayCount;
import com.team.comma.domain.track.track.dto.TrackPlayCountResponse;

import java.util.List;
import java.util.Optional;

public interface CustomTrackPlayCount {

    Optional<TrackPlayCount> findTrackPlayCountByUserEmail(String userEmail , String trackId);

    List<TrackPlayCountResponse> findTrackPlayCountByFriend(String userEmail);

    List<TrackPlayCountResponse> findTrackPlayCountByMostListenedTrack(String userEmail);

}
