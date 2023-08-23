package com.team.comma.domain.track.repository;

import com.team.comma.domain.track.domain.TrackPlayCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackPlayCountRepository extends JpaRepository<TrackPlayCount, Long> , TrackPlayCountRepositoryCustom {



}
