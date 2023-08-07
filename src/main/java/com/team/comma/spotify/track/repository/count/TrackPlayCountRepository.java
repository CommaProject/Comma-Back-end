package com.team.comma.spotify.track.repository.count;

import com.team.comma.spotify.track.domain.TrackPlayCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackPlayCountRepository extends JpaRepository<TrackPlayCount , Long> , CustomTrackPlayCount {



}
