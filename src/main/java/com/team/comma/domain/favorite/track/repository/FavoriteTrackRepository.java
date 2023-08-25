package com.team.comma.domain.favorite.track.repository;

import com.team.comma.domain.favorite.track.domain.FavoriteTrack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteTrackRepository extends JpaRepository<FavoriteTrack, Long> , FavoriteTrackRepositoryCustom {
}
