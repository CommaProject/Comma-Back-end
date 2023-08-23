package com.team.comma.domain.favorite.repository;

import com.team.comma.domain.favorite.domain.FavoriteTrack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteTrackRepository extends JpaRepository<FavoriteTrack, Long> , FavoriteTrackRepositoryCustom {
}
