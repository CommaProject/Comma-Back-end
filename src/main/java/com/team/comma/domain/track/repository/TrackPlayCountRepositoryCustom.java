package com.team.comma.domain.track.repository;

import com.team.comma.domain.track.dto.TrackPlayCountResponse;
import com.team.comma.domain.track.domain.TrackPlayCount;

import java.util.List;
import java.util.Optional;

public interface TrackPlayCountRepositoryCustom {

    Optional<TrackPlayCount> findTrackPlayCountByUserEmail(String userEmail , String trackId);

    List<TrackPlayCountResponse> findTrackPlayCountByFriend(String userEmail);

    List<TrackPlayCountResponse> findTrackPlayCountByMostListenedTrack(String userEmail);

}
