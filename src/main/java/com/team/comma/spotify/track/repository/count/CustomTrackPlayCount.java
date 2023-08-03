package com.team.comma.spotify.track.repository.count;

import com.team.comma.spotify.track.domain.TrackPlayCount;

import java.util.List;
import java.util.Optional;

public interface CustomTrackPlayCount {

    Optional<TrackPlayCount> findTrackPlayCountByUserEmail(String userEmail , String trackId);

    List<TrackPlayCount> findTrackPlayCountByFriend(String userEmail);

    List<TrackPlayCount> findTrackPlayCountByMostListenedSong(String userEmail);

}
