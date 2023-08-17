package com.team.comma.domain.spotify.track.repository.count;

import com.team.comma.domain.spotify.track.domain.TrackPlayCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackPlayCountRepository extends JpaRepository<TrackPlayCount, Long> , CustomTrackPlayCount {



}
