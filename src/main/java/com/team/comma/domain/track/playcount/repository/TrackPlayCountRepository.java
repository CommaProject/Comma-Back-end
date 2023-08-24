package com.team.comma.domain.track.playcount.repository;

import com.team.comma.domain.track.playcount.domain.TrackPlayCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackPlayCountRepository extends JpaRepository<TrackPlayCount, Long> , TrackPlayCountRepositoryCustom {



}
