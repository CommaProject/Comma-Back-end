package com.team.comma.domain.track.track.repository.count;

import com.team.comma.domain.track.trackPlayCount.TrackPlayCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackPlayCountRepository extends JpaRepository<TrackPlayCount, Long> , CustomTrackPlayCount {



}
